package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "garden_tb")
public class Garden {
    @Id
    @Column(name = "garden_id", length = 255)
    private String gardenId;

    @Column(name = "tree", length = 255)
    private String tree;

    @Column(name = "tree_grow")
    private Integer treeGrow;

    @Column(name = "tree_name", length = 50)
    private String treeName;

    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private User user;
}
