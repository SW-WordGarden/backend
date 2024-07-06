package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GardenStatus {
    private String treeName;
    private String treeStage;
    private int treeGrow;
    private String treeResult;
}
