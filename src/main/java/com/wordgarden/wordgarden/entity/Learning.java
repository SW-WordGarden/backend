package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "learning_tb")
public class Learning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", length = 255)
    private String word;

    @ManyToOne
    @JoinColumn(name = "word_id", referencedColumnName = "word_id")
    private Word wordEntity;
}
