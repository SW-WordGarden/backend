package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sq_tb")
public class Sq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sq_question", length = 255)
    private String sqQuestion;

    @Column(name = "sq_qnum")
    private int sqQnum;

    @Column(name = "sq_answer", length = 255)
    private String sqAnswer;

    @ManyToOne
    @JoinColumn(name = "sqinfo_id", referencedColumnName = "sq_id")
    private Sqinfo sqinfo;
}