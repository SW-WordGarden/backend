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
        user.setUNickname(nickname); // 이 부분 수정 필요: uNickname으로 수정
        user.setUProvider(provider); // 이 부분 수정 필요: uProvider로 수정
        return userRepository.save(user);
    }

    public User getUserByUid(String uid) {
        return userRepository.findById(uid).orElse(null);
    }
}
