package com.wordgarden.wordgarden.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WqAnswerDto {
    @JsonProperty("wqId")
    private String wqId;

    @JsonProperty("uWqA")
    private String uWqA;
}
