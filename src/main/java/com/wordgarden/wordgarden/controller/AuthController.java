package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.AuthenticationResponse;
import com.wordgarden.wordgarden.dto.LoginRequestDTO;
import com.wordgarden.wordgarden.dto.UserDto;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.security.JwtTokenProvider;
import com.wordgarden.wordgarden.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        String fcmToken = loginRequest.getFcmToken();

        User user = authService.saveOrUpdateUser(uid, nickname, provider, fcmToken);
        UserDto userDto = authService.getUserByUid(user.getUid());

        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<Object> getUserByUid(@PathVariable String uid) {
        UserDto userDto = authService.getUserByUid(uid);
        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
