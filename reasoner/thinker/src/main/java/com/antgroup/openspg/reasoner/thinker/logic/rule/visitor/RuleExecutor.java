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

package com.antgroup.openspg.reasoner.thinker.logic.rule.visitor;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Node;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.And;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Condition;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Not;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Or;
import java.util.List;
import java.util.Map;

public class RuleExecutor implements RuleNodeVisitor<Boolean> {

  @Override
  public Boolean visit(
      Or node, List<Element> spoList, Map<String, Object> context, TreeLogger logger) {
    Boolean ret = null;
    for (Node child : node.getChildren()) {
      Boolean c = child.accept(spoList, context, this, logger.addChild(child.toString()));
      c = c == null ? true : c;
      if (ret == null) {
        ret = c;
      } else {
        ret = ret || c;
      }
    }
    logger.log(ret);
    logger.setCurrentNodeRst(ret);
    return ret;
  }

  @Override
  public Boolean visit(
      And node, List<Element> spoList, Map<String, Object> context, TreeLogger logger) {
    Boolean ret = null;
    for (Node child : node.getChildren()) {
      Boolean c = child.accept(spoList, context, this, logger.addChild(child.toString()));
      c = c == null ? true : c;
      if (ret == null) {
        ret = c;
      } else {
        ret = ret && c;
      }
    }
    logger.log(ret);
    logger.setCurrentNodeRst(ret);
    return ret;
  }

  @Override
  public Boolean visit(
      Not node, List<Element> spoList, Map<String, Object> context, TreeLogger logger) {
    Boolean ret = null;
    Node child = node.getChild();
    Boolean r = child.accept(spoList, context, this, logger);
    r = r == null ? true : r;
    ret = !r;
    logger.log(ret);
    logger.setCurrentNodeRst(ret);
    return ret;
  }

  @Override
  public Boolean visit(
      Condition node, List<Element> spoList, Map<String, Object> context, TreeLogger logger) {
    Boolean ret = node.execute(spoList, context, logger);
    logger.log(ret);
    logger.setCurrentNodeRst(ret);
    return ret == null ? true : ret;
  }
}
