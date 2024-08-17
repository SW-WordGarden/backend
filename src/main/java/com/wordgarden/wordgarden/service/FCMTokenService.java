package com.wordgarden.wordgarden.service;


import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FCMTokenService {

    @Autowired
    private UserRepository userRepository;

    public void saveUserFCMToken(String uid, String token) {
        User user = userRepository.findById(uid).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFcmToken(token);
        userRepository.save(user);
    }

    public String getUserFCMToken(String uid) {
        User user = userRepository.findById(uid).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFcmToken();
    }
}
