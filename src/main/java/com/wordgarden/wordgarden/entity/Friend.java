package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;

@Entity
@Getter
@Setter
@Table(name = "friend_tb")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_id")
    private Long fId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "friend_id")
    private String friendId;  // 친구의 uid

    @Column(name = "relationship")
    private Boolean relationship;
}
