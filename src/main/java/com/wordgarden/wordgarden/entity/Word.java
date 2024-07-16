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
    @Column(name = "word_id")
    private String wordId;

    @Column(name = "word")
    private String word;

    @Column(name = "category", length = 255)
    private String category;

    @Column(name = "word_info", columnDefinition = "LONGTEXT")
    private String wordInfo;

    @Column(name = "thumbnail", columnDefinition = "LONGTEXT")
    private String thumbnail;

    @OneToMany(mappedBy = "word")
    private List<Wqinfo> wqInfos;

    @OneToMany(mappedBy = "word")
    private List<Wqwrong> wqWrongs;

    @OneToMany(mappedBy = "word")
    private List<Like> likes;
}
