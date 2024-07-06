package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizPerformance {
    private String quizId;
    private String quizType;
    private int score;
    private String date;
}
