package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wordgarden.wordgarden.dto.UserDto;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    public User saveOrUpdateUser(String uid, String nickname, String provider) {
        User user = getUserEntityByUid(uid);
        if (user == null) {
            user = new User();
            user.setUid(uid);
        }
        user.setUName(nickname);
        user.setUProvider(provider);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUid(String uid) {
        User user = userRepository.findById(uid).orElse(null);
        return user != null ? convertToDto(user) : null;
    }

    private User getUserEntityByUid(String uid) {
        return userRepository.findById(uid).orElse(null);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUid(user.getUid());
        dto.setURank(user.getURank());
        dto.setUPoint(user.getUPoint());
        dto.setUName(user.getUName());
        dto.setUImage(user.getUImage());
        dto.setUProvider(user.getUProvider());
        return dto;
    }

}
