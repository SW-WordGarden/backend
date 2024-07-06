package com.wordgarden.wordgarden.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "word_tb")
@Getter
@Setter
public class Word {
    @Id
    private String wordId;
    private String word;
    private String category;
}
