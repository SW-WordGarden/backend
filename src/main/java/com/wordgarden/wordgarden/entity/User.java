package com.wordgarden.wordgarden.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "user_tb")
public class User {
    @Id
    private String uid;
    private String email;
    private String nickname;
    private int point;
    private int u_rank;
    private String u_url;
}
