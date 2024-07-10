package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "sqresult_tb")
public class Sqresult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "u_sq_a", length = 255)
    private String uSqA;

    @Column(name = "sq_check")
    private Boolean sqCheck;

    @Column(name = "time")
    private Timestamp time;

    @ManyToOne
    @JoinColumn(name = "sq_id", referencedColumnName = "sq_id")
    private Sqinfo sqinfo;

    @Column(name = "sq_qnum")
    private int sqQnum;

    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private User user;
}
