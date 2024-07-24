package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqresultDTO {
    private String question;
    private String answer;
    private int sqQnum;
    private String userAnswer;
    private Boolean correct;
}
