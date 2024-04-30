package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import java.util.List;
import java.util.Map;

public interface Thinker {
  void init(Map<String, String> params);

  List<Result> find(Element s, Element p, Element o);

  List<Result> find(Element s, Element p, Element o, Map<String, Object> context);

  List<Result> find(Element target, Map<String, Object> context);
}
