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
    @Column(name = "fu_id")
    private Long fuId;

    @Column(name = "fu_url")
    private String fuUrl;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;
}
