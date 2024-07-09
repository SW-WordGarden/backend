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
    @Column(name = "wq_id", length = 255)
    private String wqId;

    @Lob
    @Column(name = "wq_question")
    private String wqQuestion;

    @ElementCollection
    @CollectionTable(name = "wq_questions", joinColumns = @JoinColumn(name = "wq_id"))
    @Column(name = "wq_question1")
    private List<String> wqQuestion1;

}
