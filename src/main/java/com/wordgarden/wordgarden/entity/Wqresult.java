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

    @Column(name = "u_wq_a")      // 사용자 입력 답
    private String uWqA;

    @Column(name = "wq_check")      // 오답 여부
    private Boolean wqCheck;

    @Column(name = "time")      // 풀이당시시간
    private Timestamp time;

    @ManyToOne
    @JoinColumn(name = "wq_id")     // 푼 단어 퀴즈 아이디
    private Wqinfo wqInfo;

    @ManyToOne
    @JoinColumn(name = "uid")   // 푼 사용자 아이디
    private User user;

    @Override
    public String toString() {
        return "Wqresult{" +
                "wqResultId=" + wqResultId +
                ", uWqA='" + uWqA + '\'' +
                ", wqCheck=" + wqCheck +
                ", time=" + time +
                ", wqInfo=" + (wqInfo != null ? wqInfo.getWqId() : "null") +
                ", user=" + (user != null ? user.getUid() : "null") +
                '}';
    }
}
