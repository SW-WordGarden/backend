package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Generated;
import java.util.List;

@Getter
@Setter
public class SolveQuizDTO {
    private String uid;
    private String quizTitle;
    private List<AnswerDTO> answers;
}
