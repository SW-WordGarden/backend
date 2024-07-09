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

    @Column(name = "u_rank")
    private Integer uRank;

    @Column(name = "u_nickname", length = 50)
    private String uNickname;

    @Column(name = "u_email", length = 255)
    private String uEmail;

    @Column(name = "u_url", length = 255)
    private String uUrl;

    @OneToMany(mappedBy = "user")
    private List<Friend> friends;

    @OneToMany(mappedBy = "user")
    private List<Wqresult> wqresults;

    @OneToMany(mappedBy = "user")
    private List<Sqresult> sqresults;

}
