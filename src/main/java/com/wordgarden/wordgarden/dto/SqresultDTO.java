package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class SqresultDTO {
    private String userAnswer;
    private Boolean correct;
    private Timestamp time;
    private int sqQnum;
}
