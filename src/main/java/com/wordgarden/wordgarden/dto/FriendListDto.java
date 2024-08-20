package com.wordgarden.wordgarden.dto;

import lombok.Data;
import java.util.*;

@Data
public class FriendListDto {
    private String userUUrl;
    private List<FriendDto> friends;

    public FriendListDto(String userUUrl, List<FriendDto> friends) {
        this.userUUrl = userUUrl;
        this.friends = friends;
    }
}
