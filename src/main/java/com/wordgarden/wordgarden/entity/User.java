package com.wordgarden.wordgarden.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user_tb")
public class User {
    @Id
    @Column(name = "uid", length = 255)
    private String uid;

    @Column(name = "u_point")
    private Integer uPoint;

    @Column(name = "u_coin")
    private Integer uCoin;

    @Column(name = "u_rank")
    private Integer uRank;

    @Column(name = "u_nickname", length = 50) // 필드명 수정: u_nickname에서 uNickname으로
    private String uNickname;

    @Column(name = "u_provider", length = 255) // 필드명 수정: u_provider에서 uProvider로
    private String uProvider;

    @Column(name = "u_url", length = 255)
    private String uUrl;

    @OneToMany(mappedBy = "user")
    private List<Friend> friends;

    @OneToMany(mappedBy = "user")
    private List<Wqresult> wqresults;

    @OneToMany(mappedBy = "user")
    private List<Sqresult> sqresults;
}
