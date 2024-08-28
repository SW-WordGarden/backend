package com.wordgarden.wordgarden.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonPropertyOrder({ "plantName", "plantNum", "growthValue", "growthStage", "completionDate" })
public class PlantGrowthDTO {
    private String plantName;
    private int plantNum;
    private Integer growthValue;  // int에서 Integer로 변경
    private int growthStage;
    private LocalDate completionDate;

    // 기본 생성자
    public PlantGrowthDTO() {}

    // 모든 필드를 초기화하는 생성자
    public PlantGrowthDTO(String plantName, int plantNum, Integer growthValue, int growthStage, LocalDate completionDate) {
        this.plantName = plantName;
        this.plantNum = plantNum;
        this.growthValue = growthValue;
        this.growthStage = growthStage;
        this.completionDate = completionDate;
    }
}
