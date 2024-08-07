package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "wqresult_tb")
public class Wqresult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wqresult_id")
    private Long wqResultId;

    @Column(name = "wq_a")
    private String wqA;

    @Column(name = "wq_check")
    private Boolean wqCheck;

    @Column(name = "time")
    private Timestamp time;

    @ManyToOne
    @JoinColumn(name = "wq_id")
    private Wqinfo wqInfo;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;
}
