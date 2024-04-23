package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.List;
import java.util.Map;

public interface TripleStore {
  void init(Map<String, String> param);

  List<Triple> find(Element s, Element p, Element o);
}
