package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import lombok.Data;

@Data
public class EntityPattern<K> implements ClauseEntry {
  private Entity<K> entity;
}
