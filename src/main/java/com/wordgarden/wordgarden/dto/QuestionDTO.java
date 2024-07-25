package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDTO {
    private Long id;
    private String question;
    private int questionNumber;

    public QuestionDTO(Long id, String question, int questionNumber) {
        this.id = id;
        this.question = question;
        this.questionNumber = questionNumber;
    }
}
