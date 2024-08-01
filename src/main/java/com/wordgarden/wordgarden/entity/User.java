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
    @Column(name = "uid")
    private String uid;

    @Column(name = "u_rank")
    private Integer uRank;

    @Column(name = "u_point")
    private Integer uPoint;

    @Column(name = "u_name")
    private String uName;

    @Column(name = "u_image", columnDefinition = "LONGTEXT")
    private String uImage;

    @Column(name = "u_url", length = 500)
    private String uUrl;

    @Column(name = "u_provider")
    private String uProvider;

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
