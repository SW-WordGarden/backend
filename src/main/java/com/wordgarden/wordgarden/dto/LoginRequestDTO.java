package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String uid;
    private String nickname;
    private String provider;
    private String fcmToken;
}
