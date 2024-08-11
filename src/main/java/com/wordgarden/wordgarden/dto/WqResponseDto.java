package com.wordgarden.wordgarden.dto;

import lombok.Data;

import java.util.List;

// 단어 문제
@Data
public class WqResponseDto {
    private String wqId;
    private String wqQuestion;
    private String wqTitle;
    private String wordId;
    private String word;
    private String questionType; // "four", "write", or "ox"
    private List<String> options; // For multiple choice questions
}
