package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_tb")
@Getter
@Setter
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")  // 알람 고유 아이디
    private Long alarmId;

    @Column(name = "content", length = 2000) // wq_title이나 sq_id가 들어감
    private String content;

    @Column(name = "is_read")   // 알람 확인 여부
    private Boolean isRead;

    @Column(name = "create_time")   // 알람 생성 시간
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY)  //공유한 사용자의 아이디
    @JoinColumn(name = "from_id")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)  // 받는 사용자의 아이디
    @JoinColumn(name = "to_id")
    private User toUser;
}
