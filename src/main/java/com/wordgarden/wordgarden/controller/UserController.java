package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.service.UserService;
import com.wordgarden.wordgarden.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
public class UserController {

    @Autowired
    private UserService userService;

    // 유저 정보 조회
    @GetMapping("/{uid}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String uid) {
        UserProfile userProfile = userService.getUserProfile(uid);
        return ResponseEntity.ok(userProfile);
    }

    // 유저 정보 수정
    @PutMapping("/{uid}")
    public ResponseEntity<String> updateUserProfile(@PathVariable String uid, @RequestBody UserProfile userProfile) {
        userService.updateUserProfile(uid, userProfile);
        return ResponseEntity.ok("User profile updated successfully");
    }

    // 이번 주 푼 자체 퀴즈 성적 조회
    @GetMapping("/{uid}/weekly-quiz-performance")
    public ResponseEntity<QuizPerformance> getWeeklyQuizPerformance(@PathVariable String uid) {
        QuizPerformance performance = userService.getWeeklyQuizPerformance(uid);
        return ResponseEntity.ok(performance);
    }

    // 내가 낸/푼 퀴즈 조회 (최신 1개)
    @GetMapping("/{uid}/recent-quiz")
    public ResponseEntity<List<QuizPerformance>> getRecentQuizzes(@PathVariable String uid) {
        List<QuizPerformance> quizzes = userService.getRecentQuizzes(uid);
        return ResponseEntity.ok(quizzes);
    }

    // 친구 목록 조회
    @GetMapping("/{uid}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable String uid) {
        List<User> friends = userService.getFriends(uid);
        return ResponseEntity.ok(friends);
    }

    // 친구 추가
    @PostMapping("/{uid}/friends")
    public ResponseEntity<String> addFriend(@PathVariable String uid, @RequestParam String friendUid) {
        userService.addFriend(uid, friendUid);
        return ResponseEntity.ok("Friend added successfully");
    }

    // 친구 삭제
    @DeleteMapping("/{uid}/friends/{friendUid}")
    public ResponseEntity<String> deleteFriend(@PathVariable String uid, @PathVariable String friendUid) {
        userService.deleteFriend(uid, friendUid);
        return ResponseEntity.ok("Friend deleted successfully");
    }

    // 설정 조회
    @GetMapping("/{uid}/settings")
    public ResponseEntity<UserProfile.Settings> getSettings(@PathVariable String uid) {
        UserProfile.Settings settings = userService.getSettings(uid);
        return ResponseEntity.ok(settings);
    }

    // 설정 수정
    @PutMapping("/{uid}/settings")
    public ResponseEntity<String> updateSettings(@PathVariable String uid, @RequestBody UserProfile.Settings settings) {
        userService.updateSettings(uid, settings);
        return ResponseEntity.ok("Settings updated successfully");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String uid) {
        userService.logout(uid);
        return ResponseEntity.ok("Logged out successfully");
    }

    // 회원탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String uid) {
        userService.deleteUser(uid);
        return ResponseEntity.ok("User deleted successfully");
    }
}
