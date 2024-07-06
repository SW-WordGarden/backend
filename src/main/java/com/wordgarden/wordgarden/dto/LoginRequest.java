package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String uid;
    private String email;
}
