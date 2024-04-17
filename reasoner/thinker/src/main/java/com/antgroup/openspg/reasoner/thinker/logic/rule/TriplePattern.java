package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import lombok.Data;

@Data
public class TriplePattern implements ClauseEntry {
  private Triple triple;
}
