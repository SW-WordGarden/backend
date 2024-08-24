package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "garden_tb")
public class Garden {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "garden_id", length = 36)
    private String gardenId;

    @Column(name = "tree", length = 255)
    private String tree;

    // 사용자가 물 뿌리개 갯수 추가 필요
    @Column(name = "water")
    private Integer water;

    @Column(name = "tree_grow")
    private Integer treeGrow;

    @Column(name = "tree_name", length = 50)
    private String treeName;

    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private User user;

    @Column(name = "coin")
    private Integer coin;

    @OneToMany(mappedBy = "garden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GardenBook> gardenBooks;
}
