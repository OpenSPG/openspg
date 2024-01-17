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

package com.antgroup.openspg.reasoner.utils;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PredicateKgGraph implements Predicate<KgGraph<IVertexId>> {

  private final Pattern kgGraphSchema;
  private final Map<String, Object> initRuleContext;
  private final List<String> ruleList;

  /** implement a predicate for KgGraph */
  public PredicateKgGraph(Pattern kgGraphSchema, List<String> ruleList) {
    this.kgGraphSchema = kgGraphSchema;
    this.initRuleContext = RunnerUtil.getKgGraphInitContext(this.kgGraphSchema);
    this.ruleList = ruleList;
  }

  @Override
  public boolean test(KgGraph<IVertexId> kgGraph) {
    Map<String, Object> context = RunnerUtil.kgGraph2Context(this.initRuleContext, kgGraph);
    return RuleRunner.getInstance().check(context, ruleList, "");
  }
}
