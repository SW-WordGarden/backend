package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "wqinfo_tb")
public class Wqinfo {
    @Id
    @Column(name = "wq_id")
    private String wqId;

    @Column(name = "wq_question", columnDefinition = "LONGTEXT")
    private String wqQuestion;

    @Column(name = "wq_answer", columnDefinition = "LONGTEXT")
    private String wqAnswer;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word;

    @OneToMany(mappedBy = "wqInfo")
    private List<Wqresult> wqResults;

    @OneToMany(mappedBy = "wqInfo")
    private List<Wqwrong> wqWrongs;

}
