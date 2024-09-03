package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.entity.Garden;
import com.wordgarden.wordgarden.entity.GardenBook;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.GardenBookRepository;
import com.wordgarden.wordgarden.repository.GardenRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wordgarden.wordgarden.dto.UserDto;

import java.util.*;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GardenRepository gardenRepository;
    @Autowired
    private GardenBookRepository gardenBookRepository;

    @Transactional
    public User saveOrUpdateUser(String uid, String nickname, String provider, String fcmToken) {
        User user = getUserEntityByUid(uid);
        boolean isNewUser = false;
        if (user == null) {
            user = new User();
            user.setUid(uid);
            isNewUser = true;
        }
        user.setUName(nickname);
        user.setUProvider(provider);
        user.setFcmToken(fcmToken);  // FCM 토큰 저장

        // 새 사용자인 경우 친구 코드 생성
        if (isNewUser) {
            String friendCode = generateUniqueFriendCode();
            user.setUUrl(friendCode);
        }

        user = userRepository.save(user);

        if (isNewUser) {
            createGardenForUser(user);
        }

        return user;
    }

    private String generateUniqueFriendCode() {
        String friendCode;
        do {
            friendCode = UUID.randomUUID().toString().substring(0, 8);
        } while (userRepository.existsByuUrl(friendCode));
        return friendCode;
    }

    @Transactional
    private void createGardenForUser(User user) {
        Garden garden = new Garden();
        garden.setUser(user);
        garden.setWater(0);  // 초기 물뿌리개 개수
        garden.setCoin(0);   // 초기 코인 개수
        garden.setTreeGrow(0);  // 초기 나무 성장도
        garden.setTreeName("Apple Tree");  // 초기 나무 종류
        garden = gardenRepository.save(garden);

        // 초기 GardenBook 엔트리 생성
        GardenBook initialPlant = new GardenBook();
        initialPlant.setGarden(garden);
        initialPlant.setTreeName("Apple Tree");
        initialPlant.setTreeResult("stage1");  // 초기 단계
        gardenBookRepository.save(initialPlant);
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
        dto.setUUrl(user.getUUrl());
        dto.setUParticipate(user.getUParticipate());
        return dto;
    }

}
