package com.wordgarden.wordgarden.controller;

/*
login 메서드는 프론트엔드에서 소셜 로그인 정보를 받아 AuthService의 login 메서드를 호출
setNickname 메서드는 유저가 작성한 닉네임을 받아 AuthService의 setNickname 메서드를 호출
 */

import com.wordgarden.wordgarden.dto.LoginRequest;
import com.wordgarden.wordgarden.dto.NicknameRequest;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 로그인 시 정보 받고 로그인 호출
    @PostMapping("/login")
    public ResponseEntity<User>login(@RequestBody LoginRequest loginRequest){
        User user = authService.login(loginRequest);
        return ResponseEntity.ok(user);
    }

    // 닉네임 설정 호출
    @PostMapping("/lnickname")
    public ResponseEntity<User>setNickname(@RequestBody NicknameRequest nicknameRequest){
        User user = authService.setNickname(nicknameRequest);
        return ResponseEntity.ok(user);
    }
}
