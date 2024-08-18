package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.entity.*;
import com.wordgarden.wordgarden.repository.*;
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

    private Map<String, Object> createWqQuizInfo(Wqresult wqresult) {
        Map<String, Object> quizInfo = new HashMap<>();
        quizInfo.put("type", "wq");
        quizInfo.put("title", wqresult.getWqInfo().getWqTitle());
        return quizInfo;
    }

    private Map<String, Object> createSqQuizInfo(Sqresult sqresult) {
        Map<String, Object> quizInfo = new HashMap<>();
        quizInfo.put("type", "sq");
        quizInfo.put("title", sqresult.getSqinfo().getSqTitle());
        quizInfo.put("sqId", sqresult.getSqinfo().getSqId());
        return quizInfo;
    }


    // 사용자 프로필 이미지 업데아트
    public void updateUserImage(String uid, MultipartFile image) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            user.setUImage(base64Image);
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("사용자 이미지 업데이트 실패", e);
        }
    }

    // 사용자 닉네임 업데이트
    public void updateUserNickname(String uid, String nickname) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        user.setUName(nickname);
        userRepository.save(user);
    }

    // 사용자 친구 리스트 가져오기
    public List<String> getFriendList(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<Friend> friends = friendRepository.findAllByUser(user);

        return friends.stream()
                .map(friend -> userRepository.findByUid(friend.getFriendId())
                        .map(User::getUName)
                        .orElse("Unknown"))
                .collect(Collectors.toList());
    }

    // 친구 신고
    public void reportFriend(String reporterId, String reportedId) {
        // 친구 신고 로직 구현
        System.out.println("사용자 " + reporterId + "가 사용자 " + reportedId + "를 신고했습니다");
    }

    // 마이페이지 하단에 출력될 친구(랜덤)
    private List<String> getRandomFriends(User user, int count) {
        List<Friend> allFriends = friendRepository.findAllByUser(user);
        if (allFriends.size() <= count) {
            return allFriends.stream()
                    .map(friend -> userRepository.findByUid(friend.getFriendId())
                            .map(User::getUName)
                            .orElse("Unknown"))
                    .collect(Collectors.toList());
        }

        Collections.shuffle(allFriends);
        return allFriends.subList(0, count).stream()
                .map(friend -> userRepository.findByUid(friend.getFriendId())
                        .map(User::getUName)
                        .orElse("Unknown"))
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
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uid));
        user.setULockquiz(enabled);
        userRepository.save(user);
    }
}