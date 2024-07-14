package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "word_tb")
@Getter
@Setter
public class Word {
    @Id
    @Column(name = "word_id", length = 255)
    private String wordId;

    @Column(name = "word", length = 50)
    private String word;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "description")
    private String wordInfo;

    @OneToMany(mappedBy = "word")
    private List<Like> likes;
}
