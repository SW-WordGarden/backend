package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.FriendDto;
import com.wordgarden.wordgarden.dto.FriendListDto;
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

    private static final Logger logger = LoggerFactory.getLogger(MypageController.class);

    @Autowired
    private MypageService mypageService;

    // 마이페이지에서 보여지는 사용자 정보
    @GetMapping("/info/{uid}")
    public ResponseEntity<?> getUserInfo(@PathVariable String uid) {
        return ResponseEntity.ok(mypageService.getUserInfo(uid));
    }

    // 이미지 업데이트
    @PatchMapping("/image/{uid}")
    public ResponseEntity<?> updateUserImage(@PathVariable String uid, @RequestBody Map<String, String> payload) {
        String base64Image = payload.get("image");
        if (base64Image == null || base64Image.isEmpty()) {
            return ResponseEntity.badRequest().body("Image data is required");
        }
        try {
            mypageService.updateUserImage(uid, base64Image);
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
    public ResponseEntity<FriendListDto> getFriendList(@PathVariable String uid) {
        return ResponseEntity.ok(mypageService.getFriendListWithUserUUrl(uid));
    }


    // 잠금 화면 퀴즈 설정


    @PostMapping("/{uid}/lockquiz")
    public ResponseEntity<?> updateLockScreenQuizSetting(@PathVariable String uid, @RequestParam boolean enabled) {
        logger.info("Received request to update lock screen quiz setting. UID: {}, Enabled: {}", uid, enabled);
        try {
            mypageService.updateLockScreenQuizSetting(uid, enabled);
            logger.info("Service method called successfully for UID: {}", uid);
            return ResponseEntity.ok("잠금화면 퀴즈 설정이 업데이트되었습니다.");
        } catch (Exception e) {
            logger.error("Failed to update lock screen quiz setting for UID: {}. Error: {}", uid, e.getMessage(), e);
            return ResponseEntity.badRequest().body("설정 업데이트 실패: " + e.getMessage());
        }
    }

    // 친구 추가
    @PostMapping("/friend/add")
    public ResponseEntity<?> addFriend(@RequestBody Map<String, String> request) {
        String uid = request.get("uid");
        String friendUrl = request.get("friendUrl");
        try {
            mypageService.addFriend(uid, friendUrl);
            return ResponseEntity.ok("친구가 추가되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 친구 삭제
    @DeleteMapping("/friend/delete")
    public ResponseEntity<?> deleteFriend(@RequestBody Map<String, String> request) {
        String uid = request.get("uid");
        String friendId = request.get("friendId");
        try {
            mypageService.deleteFriend(uid, friendId);
            return ResponseEntity.ok("친구가 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 친구 신고
    @PostMapping("/report")
    public ResponseEntity<?> reportFriend(@RequestBody Map<String, String> reportInfo) {
        String reporterId = reportInfo.get("reporterId");
        String reportedId = reportInfo.get("reportedId");
        String reason = reportInfo.get("reason");  // 신고 사유

        if (reporterId == null || reportedId == null || reason == null) {
            return ResponseEntity.badRequest().body("Reporter ID, reported ID, and reason are required");
        }

        try {
            mypageService.reportFriend(reporterId, reportedId, reason);
            return ResponseEntity.ok("친구가 신고되었습니다. 퀴즈 공유가 제한됩니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}