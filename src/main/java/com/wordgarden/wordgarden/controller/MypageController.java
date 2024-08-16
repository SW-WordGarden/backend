package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.service.MypageService;
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

    @GetMapping("/info/{uid}")
    public ResponseEntity<?> getUserInfo(@PathVariable String uid) {
        return ResponseEntity.ok(mypageService.getUserInfo(uid));
    }

    @PatchMapping("/image/{uid}")
    public ResponseEntity<?> updateUserImage(@PathVariable String uid, @RequestPart("image") MultipartFile image) {
        try {
            mypageService.updateUserImage(uid, image);
            return ResponseEntity.ok("Profile changed");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/nickname/{uid}")
    public ResponseEntity<?> updateUserNickname(@PathVariable String uid, @RequestBody Map<String, String> payload) {
        String nickname = payload.get("nickname");
        if (nickname == null || nickname.isEmpty()) {
            return ResponseEntity.badRequest().body("Nickname didn't change");
        }
        mypageService.updateUserNickname(uid, nickname);
        return ResponseEntity.ok("nickname changed");
    }

    @GetMapping("/friends/{uid}")
    public ResponseEntity<List<String>> getFriendList(@PathVariable String uid) {
        return ResponseEntity.ok(mypageService.getFriendList(uid));
    }

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
}