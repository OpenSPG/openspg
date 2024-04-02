package com.antgroup.openspg.reasoner.thinker.logic.graph;

import lombok.Data;

@Data
public class Value implements Element {
    private String name;
    private Object val;

    public Value() {
    }

    public Value(String name, Object val) {
        this.name = name;
        this.val = val;
    }
}
