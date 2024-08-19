package com.wordgarden.wordgarden.dto;

import lombok.Data;

@Data
public class OneQuizCorrectDto {
    private String wqId;
    private String wordId;
    private String word;
    private String wordInfo;
    private String correctAnswer;
    private String userAnswer;
    private Boolean isCorrect;
}
