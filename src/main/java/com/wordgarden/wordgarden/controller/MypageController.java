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

    @PutMapping("/image/{uid}")
    public ResponseEntity<?> updateUserImage(@PathVariable String uid, @RequestParam("image") MultipartFile image) {
        mypageService.updateUserImage(uid, image);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/nickname/{uid}")
    public ResponseEntity<?> updateUserNickname(@PathVariable String uid, @RequestParam("nickname") String nickname) {
        mypageService.updateUserNickname(uid, nickname);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friends/{uid}")
    public ResponseEntity<List<String>> getFriendList(@PathVariable String uid) {
        return ResponseEntity.ok(mypageService.getFriendList(uid));
    }

    @PostMapping("/report")
    public ResponseEntity<?> reportFriend(@RequestBody Map<String, String> reportInfo) {
        mypageService.reportFriend(reportInfo.get("reporterId"), reportInfo.get("reportedId"));
        return ResponseEntity.ok().build();
    }
}