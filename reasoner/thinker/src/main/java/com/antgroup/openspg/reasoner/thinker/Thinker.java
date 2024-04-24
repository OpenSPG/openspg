package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import java.util.Map;

public interface Thinker<K> {
  void init(Map<String, String> params);

  Result find(Element s, Element p, Element o);

  Result find(Element s, Element p, Element o, Map<String, Object> context);
}
