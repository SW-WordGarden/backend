package com.wordgarden.wordgarden.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wqwrong_tb")
public class Wqwrong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wqw_id")
    private Long wqwId;

    @ManyToOne
    @JoinColumn(name = "wq_id")
    private Wqinfo wqInfo;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

}
