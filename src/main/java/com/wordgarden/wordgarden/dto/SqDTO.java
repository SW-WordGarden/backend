package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SqDTO {
    private String uid;
    private String quizTitle;
    private List<QuestionAnswerDTO> questionsAndAnswers;
}
