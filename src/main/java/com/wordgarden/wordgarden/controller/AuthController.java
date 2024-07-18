package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.LoginRequestDTO;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.security.JwtTokenProvider;
import com.wordgarden.wordgarden.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequestDTO loginRequest) {
        String uid = loginRequest.getUid();
        String nickname = loginRequest.getNickname();
        String provider = loginRequest.getProvider();

        // 처리 로직
        User user = authService.saveOrUpdateUser(uid, nickname, provider);

        return ResponseEntity.ok(user);
    }


    @GetMapping("/user")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (jwtTokenProvider.validateToken(token)) {
            String uid = jwtTokenProvider.getUidFromToken(token);
            User user = authService.getUserByUid(uid);
            if (user != null) {
                return ResponseEntity.ok(user);
            }
        }

        return ResponseEntity.notFound().build();
    }
}
