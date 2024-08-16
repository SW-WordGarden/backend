package com.wordgarden.wordgarden.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "uid")   // 사용자 아이디
    private String uid;

    @Column(name = "u_rank")    // 등수
    private Integer uRank;

    @Column(name = "u_point")   // 포인트
    private Integer uPoint = 0;

    @Column(name = "u_name")    // 닉네임
    private String uName;

    @Column(name = "u_image", columnDefinition = "LONGTEXT")    //썸네일
    private String uImage;

    @Column(name = "u_url", length = 500)
    private String uUrl;

    @Column(name = "u_provider")
    private String uProvider;   // 소셜로그인 플랫폼

    @JsonIgnore
    // 또는 @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Wqresult> wqResults;

    @OneToMany(mappedBy = "user")
    private List<Wqwrong> wqWrongs;

    @OneToMany(mappedBy = "user")
    private List<Friend> friends;

    @OneToMany(mappedBy = "user")
    private List<Like> likes;
}
