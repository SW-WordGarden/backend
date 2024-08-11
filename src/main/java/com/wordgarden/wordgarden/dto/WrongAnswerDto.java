package com.wordgarden.wordgarden.dto;

import lombok.Data;

// 단어 퀴즈시 틀린 단어
@Data
public class WrongAnswerDto {
    private String wqId;
    private String word;
    private String wordInfo;
}
