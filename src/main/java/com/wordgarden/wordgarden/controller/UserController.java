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

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public User getMypage(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // "Bearer " 이후 부분 추출
        String username = jwtUtil.extractUsername(jwt);
        return userService.getUserByUsername(username);
    }
}
