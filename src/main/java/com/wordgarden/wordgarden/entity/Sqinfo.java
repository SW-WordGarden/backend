package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "sqinfo_tb")
public class Sqinfo {
    @Id
    @Column(name = "sq_id", length = 300)
    private String sqId;

    @Column(name = "sq_title", length = 255)
    private String sqTitle;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    @OneToMany(mappedBy = "sqinfo")
    private List<Sq> sqs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 기본 생성자 추가
    public Sqinfo() {
        this.createdAt = LocalDateTime.now();
    }

    // sq_id 생성 메소드
    public void generateSqId(long index) {
        this.sqId = this.sqTitle + "_" + index;
    }
}
