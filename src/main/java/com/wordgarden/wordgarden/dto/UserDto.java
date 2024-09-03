package com.wordgarden.wordgarden.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserDto {
    private String uid;
    private Integer uRank;
    private Integer uPoint;
    private String uName;
    private String uImage;
    private String uProvider;
    private String uUrl;
    private Integer uParticipant;
}
