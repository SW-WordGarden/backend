package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    public User saveOrUpdateUser(String uid, String nickname, String provider) {
        User user = userRepository.findById(uid).orElse(new User());
        user.setUid(uid);
        user.setUNickname(nickname);
        user.setUProvider(provider);
        return userRepository.save(user);
    }

    public User getUserByUid(String uid) {
        return userRepository.findById(uid).orElse(null);
    }
}
