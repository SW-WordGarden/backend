package com.wordgarden.wordgarden.dto;

import lombok.Data;

@Data
public class UserGardenInfoDTO {
    private int coins;        // 사용자가 보유한 코인 수
    private int wateringCans; // 사용자가 보유한 물뿌리개 수
    private long plantCount;   // 사용자가 도감에 등록한 식물 이미지 수

    // 기본 생성자 추가
    public UserGardenInfoDTO() {}

    // long 타입의 plantCount를 받는 생성자
    public UserGardenInfoDTO(int coins, int wateringCans, long plantCount) {
        this.coins = coins;
        this.wateringCans = wateringCans;
        this.plantCount = plantCount;
    }

    // int 타입의 plantCount를 받는 생성자 (하위 호환성을 위해 유지)
    public UserGardenInfoDTO(int coins, int wateringCans, int plantCount) {
        this(coins, wateringCans, (long) plantCount);
    }
}
