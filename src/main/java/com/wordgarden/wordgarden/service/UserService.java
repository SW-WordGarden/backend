package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private FriendRepository friendRepository;

    public UserProfile getUserProfile(String uid) {
        User user = userRepository.findById(uid).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserProfile(user);
    }

    public void updateUserProfile(String uid, UserProfile userProfile) {
        User user = userRepository.findById(uid).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setNickname(userProfile.getNickname());
        userRepository.save(user);
    }

    public QuizPerformance getWeeklyQuizPerformance(String uid) {
        // 이번 주 푼 자체 퀴즈 성적 조회
        return quizResultRepository.findWeeklyPerformanceByUid(uid);
    }

    public List<QuizPerformance> getRecentQuizzes(String uid) {
        // 내가 낸/푼 퀴즈 조회 (최신 1개)
        return quizResultRepository.findRecentQuizzesByUid(uid);
    }

    public List<User> getFriends(String uid) {
        return friendRepository.findFriendsByUid(uid);
    }

    public void addFriend(String uid, String friendUid) {
        friendRepository.addFriend(uid, friendUid);
    }

    public void deleteFriend(String uid, String friendUid) {
        friendRepository.deleteFriend(uid, friendUid);
    }

    public UserProfile.Settings getSettings(String uid) {
        User user = userRepository.findById(uid).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getSettings();
    }

    public void updateSettings(String uid, UserProfile.Settings settings) {
        User user = userRepository.findById(uid).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setSettings(settings);
        userRepository.save(user);
    }

    public void logout(String uid) {
        // 로그아웃 로직 구현
    }

    public void deleteUser(String uid) {
        userRepository.deleteById(uid);
    }
}