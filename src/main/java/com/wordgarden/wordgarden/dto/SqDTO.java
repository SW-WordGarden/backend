package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SqDTO {
    private String uid;
    private String quizTitle;
    private String sqId;  // 새로 추가
    private List<QuestionAnswerDTO> questionsAndAnswers;
    private List<SqresultDTO> sqresults;
}
