package com.wordgarden.wordgarden.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 단어 퀴즈 제출
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WqSubmissionDto {
    @JsonProperty("uid")
    private String uid;

    @JsonProperty("answers")
    private List<WqAnswerDto> answers;
}
