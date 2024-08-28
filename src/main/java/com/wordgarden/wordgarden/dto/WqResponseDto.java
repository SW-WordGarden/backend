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
    private List<String> options; // 4지선다 선택지
    private String userAnswer;
    private String correctAnswer;
    private String correctInfo;    // 단어의 뜻
}
