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

package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.SimplifyThinkerParser;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.rule.visitor.RuleExecutor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class RuleExecutorTests {
  private Rule getR1() {
    String rule =
        "Define (危险水平分层/`很高危`) {\n"
            + "  R1:高血压分层/`临床并发症` and (\"有并发症的糖尿病\" in 症状) and 伸缩压>=140\n"
            + "}";
    SimplifyThinkerParser parser = new SimplifyThinkerParser();
    return parser.parseSimplifyDsl(rule, null).head();
  }

  private Rule getR2() {
    String rule = "Define (:Medical.ExaminationTerm/`孕酮`)-[:abnormalValue]->(: Medical.AbnormalExaminationIndicator/`偏高`) {\n" +
            "  卵泡期 AND (孕酮 > 1.52)\n" +
            "}";
    SimplifyThinkerParser parser = new SimplifyThinkerParser();
    return parser.parseSimplifyDsl(rule, null).head();
  }

  @Test
  public void testRuleExe() {
    Rule rule = getR1();
    Node root = rule.getRoot();
    TreeLogger logger = new TreeLogger(root.toString());
    Map<String, Object> session = new HashMap<>();
    session.put("伸缩压", 141);
    session.put("症状", "有并发症的糖尿病");
    Boolean ret =
        rule.getRoot()
            .accept(
                Arrays.asList(new Entity("临床并发症", "高血压分层")), session, new RuleExecutor(), logger);
    Assert.assertTrue(ret);
  }

  @Test
  public void testRuleAbsent() {
    Rule rule = getR2();
    Node root = rule.getRoot();
    TreeLogger logger = new TreeLogger(root.toString());
    Map<String, Object> session = new HashMap<>();
    session.put("孕酮", 14.29);
    Boolean ret = rule.getRoot().accept(new LinkedList<>(), session, new RuleExecutor(true), logger);
    Assert.assertTrue(!ret);
    for (TreeLogger log : logger.getChildren()) {
      if (log.getCurrentNodeName().equals("卵泡期")) {
        Assert.assertTrue(log.getCurrentNodeRst() == null);
        Assert.assertTrue(log.getCurrentNodeMsg().equals("卵泡期"));
      }
    }
  }
}
