package com.wordgarden.wordgarden.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PlantGrowthDTO {
    private String plantName;     // 현재 키우고 있는 식물의 이름
    private int plantNum;
    private int growthValue;      // 현재 식물의 성장값
    private int growthStage;      // 현재 식물의 성장 단계 (1~4)
    private LocalDate completionDate; // 식물이 완성된 날짜 (4단계 도달 시)

    // 기본 생성자
    public PlantGrowthDTO() {}

    // 모든 필드를 초기화하는 생성자
    public PlantGrowthDTO(String plantName, int growthValue, int growthStage, LocalDate completionDate) {
        this.plantName = plantName;
        this.plantNum = plantNum;
        this.growthValue = growthValue;
        this.growthStage = growthStage;
        this.completionDate = completionDate;
    }
}
