package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.service.MypageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class MypageController {

    @Autowired
    private MypageService mypageService;

    // 마이페이지에서 보여지는 사용자 정보
    @GetMapping("/info/{uid}")
    public ResponseEntity<?> getUserInfo(@PathVariable String uid) {
        return ResponseEntity.ok(mypageService.getUserInfo(uid));
    }

    // 이미지 업데이트
    @PatchMapping("/image/{uid}")
    public ResponseEntity<?> updateUserImage(@PathVariable String uid, @RequestPart("image") MultipartFile image) {
        try {
            mypageService.updateUserImage(uid, image);
            return ResponseEntity.ok("Profile changed");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 닉네임 업데이트
    @PatchMapping("/nickname/{uid}")
    public ResponseEntity<?> updateUserNickname(@PathVariable String uid, @RequestBody Map<String, String> payload) {
        String nickname = payload.get("nickname");
        if (nickname == null || nickname.isEmpty()) {
            return ResponseEntity.badRequest().body("Nickname didn't change");
        }
        mypageService.updateUserNickname(uid, nickname);
        return ResponseEntity.ok("nickname changed");
    }

    // 친구 리스트 반환
    @GetMapping("/friends/{uid}")
    public ResponseEntity<List<String>> getFriendList(@PathVariable String uid) {
        return ResponseEntity.ok(mypageService.getFriendList(uid));
    }

    // 사용자 신고
    @PatchMapping("/report")
    public ResponseEntity<?> reportFriend(@RequestBody Map<String, String> reportInfo) {
        String reporterId = reportInfo.get("reporterId");
        String reportedId = reportInfo.get("reportedId");

        if (reporterId == null || reportedId == null) {
            return ResponseEntity.badRequest().body("Reporter ID and reported ID are required");
        }

        mypageService.reportFriend(reporterId, reportedId);
        return ResponseEntity.ok("Friend reported successfully");
    }

    // 잠금 화면 퀴즈 설정
    private static final Logger logger = LoggerFactory.getLogger(MypageController.class);

    @PostMapping("/{uid}/lockquiz")
    public ResponseEntity<?> updateLockScreenQuizSetting(@PathVariable String uid, @RequestParam boolean enabled) {
        logger.info("Received request to update lock screen quiz setting. UID: {}, Enabled: {}", uid, enabled);
        try {
            // 기존 코드
            logger.info("Successfully updated lock screen quiz setting for UID: {}", uid);
            return ResponseEntity.ok("잠금화면 퀴즈 설정이 업데이트되었습니다.");
        } catch (Exception e) {
            logger.error("Failed to update lock screen quiz setting for UID: {}. Error: {}", uid, e.getMessage(), e);
            return ResponseEntity.badRequest().body("설정 업데이트 실패: " + e.getMessage());
        }
    }
}