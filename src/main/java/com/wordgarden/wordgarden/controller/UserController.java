package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.AuthenticationResponse;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        String token = JwtUtil.generateToken(savedUser.getUid());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
