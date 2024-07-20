package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.AuthenticationResponse;
import com.wordgarden.wordgarden.dto.LoginRequestDTO;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.security.JwtTokenProvider;
import com.wordgarden.wordgarden.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        String uid = loginRequest.getUid();
        String nickname = loginRequest.getNickname();
        String provider = loginRequest.getProvider();

        // 처리 로직
        User user = authService.saveOrUpdateUser(uid, nickname, provider);

        // AuthenticationResponse 객체 생성 및 반환
        return ResponseEntity.ok(user);
    }


    @GetMapping("/user")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("X-User-UID") String uid) {
        User user = authService.getUserByUid(uid);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
}
