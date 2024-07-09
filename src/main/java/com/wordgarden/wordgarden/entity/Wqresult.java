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
    private Long id;

    @Column(name = "u_wq_a", length = 255)
    private String uWqA;

    @Column(name = "wq_check")
    private Boolean wqCheck;

    @Column(name = "time")
    private Timestamp time;

    @ManyToOne
    @JoinColumn(name = "wq_id", referencedColumnName = "wq_id")
    private Wqinfo wqinfo;

    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private User user;
}
