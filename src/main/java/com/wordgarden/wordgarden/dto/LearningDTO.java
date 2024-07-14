package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningDTO {
    private Long id;
    private String word;
    private String wordId;
    private String category;
    private String wordInfo;
}
