package com.wordgarden.wordgarden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "gardenbook_tb")
public class GardenBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 보유 식물 이미지 / 도감 이미지 개수

    @Column(name = "tree_result", length = 255)
    private String treeResult;

    @Column(name = "tree_name", length = 50)
    private String treeName;

    @ManyToOne
    @JoinColumn(name = "garden_id", referencedColumnName = "garden_id")
    private Garden garden;

}
