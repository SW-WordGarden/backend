package com.wordgarden.wordgarden.dto;

import lombok.Data;

@Data
public class FriendDto {
    private String uid;
    private String nickname;
    private String profileImg;

    public FriendDto(String uid, String nickname, String profileImg) {
        this.uid = uid;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }
}
