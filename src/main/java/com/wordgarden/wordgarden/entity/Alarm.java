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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")  // 알람 고유 아이디
    private String alarmId;

    @Column(name = "sequence")
    private Long sequence;  // 번호 저장

    @Column(name = "content", length = 2000) // wq_title이나 sq_id가 들어감
    private String content;

    @Column(name = "quiz_type")
    private String quizType;  // 퀴즈 타입

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

    @PrePersist
    public void prePersist() {  // 자바 ID 생성
        if (this.alarmId == null && this.toUser != null && this.sequence != null) {
            this.alarmId = this.toUser.getUid() + "_" + this.sequence;
        }
    }
}
