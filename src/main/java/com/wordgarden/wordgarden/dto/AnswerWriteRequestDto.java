package com.wordgarden.wordgarden.dto;

import lombok.Data;

// 주관식 답안 제출
@Data
public class AnswerWriteRequestDto {
    private boolean includeWrite;
}
