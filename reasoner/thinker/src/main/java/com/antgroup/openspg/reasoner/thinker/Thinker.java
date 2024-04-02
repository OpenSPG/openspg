package com.antgroup.openspg.reasoner.thinker;

import java.util.List;
import java.util.Map;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;

public interface Thinker {
    void init(Map<String, String> params);

    List<Triple> find(Element s, Element p, Element o);
}
