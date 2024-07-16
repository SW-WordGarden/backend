package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "like_tb")
@Getter
@Setter
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid", nullable = false) // User 엔티티의 uid 필드와 매핑
    private User user;
}
