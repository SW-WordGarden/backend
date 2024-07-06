package com.wordgarden.wordgarden.service;

/*
login 메서드는 유저의 이메일을 기준으로 유저를 조회하고, 존재하지 않으면 새로운 유저를 생성
setNickname 메서드는 유저의 UID를 기준으로 유저를 조회하고, 존재하면 닉네임을 업데이트
 */

import com.wordgarden.wordgarden.dto.LoginRequest;
import com.wordgarden.wordgarden.dto.NicknameRequest;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // 유저 조회, 조회되지 않으면 생성
    public User login(LoginRequest loginRequest){
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        User user;
        if (optionalUser.isPresent()){
            user = optionalUser.get();
        } else {
            user = new User();
            user.setUid(loginRequest.getUid());
            user.setEmail(loginRequest.getEmail());
            user = userRepository.save(user);
        }
        return user;
    }

    // 유저가 존재하면 유저가 작성한 닉네임으로 유저정보 업데이트
    public User setNickname(NicknameRequest nicknameRequest){
        Optional<User> optionalUser = userRepository.findById(Long.valueOf(nicknameRequest.getUid()));
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setNickname(nicknameRequest.getNickname());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
}
