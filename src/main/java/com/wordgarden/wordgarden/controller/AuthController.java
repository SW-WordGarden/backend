package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.LoginRequestDTO;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequestDTO loginRequest) {
        String uid = loginRequest.getUid();
        String nickname = loginRequest.getNickname();
        String provider = loginRequest.getProvider();

        // 처리 로직
        User user = authService.saveOrUpdateUser(uid, nickname, provider);

        return ResponseEntity.ok(user);
    }


    @GetMapping("/user/{uid}")
    public ResponseEntity<User> getCurrentUser(@PathVariable("uid") String userId) {
        // userId를 이용하여 사용자 정보를 가져오는 로직을 작성
        User user = authService.getUserByUid(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
