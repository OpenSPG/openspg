/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */
package com.antgroup.openspg.reasoner.thinker.logic.rule.exact;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Node;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.visitor.RuleNodeVisitor;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Not implements Node {
  private Node child;

  public Not(Node child) {
    this.child = child;
  }

  @Override
  public <R> R accept(
      List<Element> spoList,
      Map<String, Object> context,
      RuleNodeVisitor<R> visitor,
      TreeLogger logger) {
    return visitor.visit(this, spoList, context, logger);
  }

  @Override
  public String toString() {
    return "NOT";
  }
}
