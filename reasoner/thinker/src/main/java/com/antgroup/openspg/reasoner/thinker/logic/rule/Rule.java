package com.antgroup.openspg.reasoner.thinker.logic.rule;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class Rule implements Serializable {
    private List<ClauseEntry> body;
    private ClauseEntry       head;
    private Node              root;
    private String            desc;
}
