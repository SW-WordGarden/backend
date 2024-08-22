package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.dto.FriendDto;
import com.wordgarden.wordgarden.dto.FriendListDto;
import com.wordgarden.wordgarden.entity.*;
import com.wordgarden.wordgarden.repository.*;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MypageService {

    private static final Logger logger = LoggerFactory.getLogger(MypageService.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private WqresultRepository wqresultRepository;
    @Autowired
    private SqinfoRepository sqinfoRepository;
    @Autowired
    private SqresultRepository sqresultRepository;

    // 마이페이지에서 보여지는 모든 사용자 정보
    public Map<String, Object> getUserInfo(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("profileImage", user.getUImage());
        userInfo.put("point", user.getUPoint());
        userInfo.put("rank", calculateUserRank(user));
        userInfo.put("randomFriends", getRandomFriends(user, 5));
        userInfo.put("name", user.getUName());
        userInfo.put("uUrl", user.getUUrl());

        // 7일간의 퀴즈 결과 통계
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Wqresult> recentResults = wqresultRepository.findByUserAndTimeAfter(user, sevenDaysAgo);
        userInfo.put("all", recentResults.size());
        userInfo.put("right", recentResults.stream().filter(Wqresult::getWqCheck).count());

        // 가장 최근에 만든 커스텀 문제
        sqinfoRepository.findTopByUserOrderByCreatedAtDesc(user).ifPresent(latestCustomQuiz -> {
            Map<String, String> latestQuizInfo = new HashMap<>();
            latestQuizInfo.put("sqTitle", latestCustomQuiz.getSqTitle());
            latestQuizInfo.put("sqId", latestCustomQuiz.getSqId());
            userInfo.put("latestCustomQuiz", latestQuizInfo);
        });

        // 가장 최근에 푼 퀴즈
        Map<String, Object> latestSolvedQuiz = getLatestSolvedQuiz(user);
        if (latestSolvedQuiz != null) {
            userInfo.put("latestSolvedQuiz", latestSolvedQuiz);
        }

        return userInfo;
    }

    // 가장 최근에 푼 퀴즈
    private Map<String, Object> getLatestSolvedQuiz(User user) {
        Optional<Wqresult> latestWq = wqresultRepository.findTopByUserOrderByTimeDesc(user);
        Optional<Sqresult> latestSq = sqresultRepository.findTopByUserOrderByTimeDesc(user);

        if (latestWq.isPresent() && latestSq.isPresent()) {
            if (latestWq.get().getTime().after(latestSq.get().getTime())) {
                return createWqQuizInfo(latestWq.get());
            } else {
                return createSqQuizInfo(latestSq.get());
            }
        } else if (latestWq.isPresent()) {
            return createWqQuizInfo(latestWq.get());
        } else if (latestSq.isPresent()) {
            return createSqQuizInfo(latestSq.get());
        }

        return null;
    }

    // 가장 최근에 푼 퀴즈의 타입이 wq
    private Map<String, Object> createWqQuizInfo(Wqresult wqresult) {
        Map<String, Object> quizInfo = new HashMap<>();
        quizInfo.put("type", "wq");
        quizInfo.put("title", wqresult.getWqInfo().getWqTitle());
        return quizInfo;
    }

    // 가장 최근에 푼 퀴즈의 타입이 sq
    private Map<String, Object> createSqQuizInfo(Sqresult sqresult) {
        Map<String, Object> quizInfo = new HashMap<>();
        quizInfo.put("type", "sq");
        quizInfo.put("title", sqresult.getSqinfo().getSqTitle());
        quizInfo.put("sqId", sqresult.getSqinfo().getSqId());
        return quizInfo;
    }


    // 사용자 프로필 이미지 업데이트
    @Transactional
    public void updateUserImage(String uid, String base64Image) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // Base64 문자열을 그대로 저장
        user.setUImage(base64Image);
        userRepository.save(user);
    }

    // 사용자 닉네임 업데이트
    public void updateUserNickname(String uid, String nickname) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        user.setUName(nickname);
        userRepository.save(user);
    }

    // 사용자 친구 리스트 가져오기
    public FriendListDto getFriendListWithUserUUrl(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<FriendDto> friendList = getFriendList(uid);
        return new FriendListDto(user.getUUrl(), friendList);
    }

    public List<FriendDto> getFriendList(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<Friend> friends = friendRepository.findAllByUser(user);

        return friends.stream()
                .map(friend -> userRepository.findByUid(friend.getFriendId())
                        .map(u -> new FriendDto(u.getUid(), u.getUName(), u.getUImage()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 마이페이지 하단에 출력될 친구(랜덤)
    private List<FriendDto> getRandomFriends(User user, int count) {
        List<Friend> allFriends = friendRepository.findAllByUser(user);
        if (allFriends.size() <= count) {
            return allFriends.stream()
                    .map(friend -> userRepository.findByUid(friend.getFriendId())
                            .map(u -> new FriendDto(u.getUid(), u.getUName(), u.getUImage()))
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        Collections.shuffle(allFriends);
        return allFriends.subList(0, count).stream()
                .map(friend -> userRepository.findByUid(friend.getFriendId())
                        .map(u -> new FriendDto(u.getUid(), u.getUName(), u.getUImage()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 등수 계산하기
    private int calculateUserRank(User user) {
        List<User> allUsers = userRepository.findAll();
        allUsers.sort((u1, u2) -> u2.getUPoint().compareTo(u1.getUPoint()));
        return allUsers.indexOf(user) + 1;
    }

    // 잠금화면 퀴즈 설정
    @Transactional
    public void updateLockScreenQuizSetting(String uid, boolean enabled) {
        logger.info("Updating lock screen quiz setting for user: {}, enabled: {}", uid, enabled);
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uid));

        logger.info("Current lock screen quiz setting for user {}: {}", uid, user.getULockquiz());

        user.setULockquiz(enabled);
        user = userRepository.save(user);

        logger.info("After save, lock screen quiz setting for user {}: {}", uid, user.getULockquiz());

        entityManager.flush(); // 명시적으로 변경사항을 데이터베이스에 반영
        entityManager.clear(); // 영속성 컨텍스트를 클리어

        // 변경 후 즉시 다시 조회하여 확인
        User updatedUser = userRepository.findById(uid).orElseThrow();
        logger.info("After flush and clear, verified lock screen quiz setting for user {}: {}", uid, updatedUser.getULockquiz());
    }

    // 친구 추가
    @Transactional
    public void addFriend(String uid, String friendUrl) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        User friend = userRepository.findByuUrl(friendUrl)  // 메소드 이름 수정
                .orElseThrow(() -> new RuntimeException("해당 URL의 사용자를 찾을 수 없습니다."));

        if (friendRepository.existsByUserAndFriendId(user, friend.getUid())) {
            throw new RuntimeException("이미 친구로 등록된 사용자입니다.");
        }

        // 사용자 -> 친구 방향의 관계 설정
        Friend newFriend = new Friend();
        newFriend.setUser(user);
        newFriend.setFriendId(friend.getUid());
        newFriend.setRelationship(true);
        friendRepository.save(newFriend);

        // 친구 -> 사용자 방향의 관계 설정 (양방향 관계)
        Friend reverseFriend = new Friend();
        reverseFriend.setUser(friend);
        reverseFriend.setFriendId(user.getUid());
        reverseFriend.setRelationship(true);
        friendRepository.save(reverseFriend);
    }

    // 친구 삭제
    @Transactional
    public void deleteFriend(String uid, String friendId) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        User friend = userRepository.findByUid(friendId)
                .orElseThrow(() -> new RuntimeException("친구를 찾을 수 없습니다."));

        // 사용자 -> 친구 방향의 관계 삭제
        Friend userToFriend = friendRepository.findByUserAndFriendId(user, friendId)
                .orElseThrow(() -> new RuntimeException("해당 친구 관계를 찾을 수 없습니다."));
        friendRepository.delete(userToFriend);

        // 친구 -> 사용자 방향의 관계 삭제
        Friend friendToUser = friendRepository.findByUserAndFriendId(friend, uid)
                .orElseThrow(() -> new RuntimeException("해당 친구 관계를 찾을 수 없습니다."));
        friendRepository.delete(friendToUser);
    }

    // 친구 신고
    @Transactional
    public void reportFriend(String reporterId, String reportedId, String reason) {
        User reporter = userRepository.findByUid(reporterId)
                .orElseThrow(() -> new RuntimeException("신고자를 찾을 수 없습니다."));
        User reported = userRepository.findByUid(reportedId)
                .orElseThrow(() -> new RuntimeException("신고된 사용자를 찾을 수 없습니다."));

        Friend friendRelation = friendRepository.findByUserAndFriendId(reporter, reportedId)
                .orElseThrow(() -> new RuntimeException("친구 관계를 찾을 수 없습니다."));

        friendRelation.setRelationship(false);  // false로 설정하여 퀴즈 공유 제한
        friendRelation.setReportReason(reason);  // 신고 사유 설정
        friendRepository.save(friendRelation);

        // 역방향 관계도 처리
        Friend reverseFriendRelation = friendRepository.findByUserAndFriendId(reported, reporterId)
                .orElseThrow(() -> new RuntimeException("역방향 친구 관계를 찾을 수 없습니다."));
        reverseFriendRelation.setRelationship(false);
        reverseFriendRelation.setReportReason(reason);  // 역방향 관계에도 신고 사유 설정
        friendRepository.save(reverseFriendRelation);
    }

    // FCM엡데이트
    @Transactional
    public boolean updateFcmToken(String uid, String newFcmToken) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        if (!Objects.equals(user.getFcmToken(), newFcmToken)) {
            user.setFcmToken(newFcmToken);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}