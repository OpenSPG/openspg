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
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.And;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Condition;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Not;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Or;
import java.util.List;
import java.util.Map;

public interface RuleNodeVisitor<R> {
  abstract R visit(Or node, List<Element> spoList, Map<String, Object> context, TreeLogger logger);

  abstract R visit(And node, List<Element> spoList, Map<String, Object> context, TreeLogger logger);

  abstract R visit(Not node, List<Element> spoList, Map<String, Object> context, TreeLogger logger);

  abstract R visit(
      Condition node, List<Element> spoList, Map<String, Object> context, TreeLogger logger);
}
