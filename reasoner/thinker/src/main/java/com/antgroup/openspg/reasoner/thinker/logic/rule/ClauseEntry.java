package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import java.io.Serializable;

public interface ClauseEntry extends Serializable {
  Element toElement();
}
