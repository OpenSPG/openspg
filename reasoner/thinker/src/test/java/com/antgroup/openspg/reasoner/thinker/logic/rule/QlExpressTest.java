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

import com.antgroup.openspg.reasoner.thinker.qlexpress.QlExpressRunner;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class QlExpressTest {
  @Test
  public void testNumberCompString() {
    RuleRunner runner = QlExpressRunner.getInstance();
    Map<String, Object> context = new HashMap<>();
    context.put("value", "160");
    Assert.assertTrue((Boolean) runner.executeExpression(context, Arrays.asList("value>150"), ""));
  }

  @Test
  public void testNumberCompString1() {
    RuleRunner runner = QlExpressRunner.getInstance();
    Map<String, Object> context = new HashMap<>();
    context.put("value", ">160");
    Assert.assertTrue((Boolean) runner.executeExpression(context, Arrays.asList("value>150"), ""));
  }

  @Test
  public void testNumberCompString2() {
    RuleRunner runner = QlExpressRunner.getInstance();
    Map<String, Object> context = new HashMap<>();
    context.put("value", "<160");
    Assert.assertTrue(runner.executeExpression(context, Arrays.asList("value>150"), "") == null);
  }

  @Test
  public void testNumberCompString3() {
    RuleRunner runner = QlExpressRunner.getInstance();
    Map<String, Object> context = new HashMap<>();
    context.put("value", "<160ng/ml");
    Assert.assertTrue(runner.executeExpression(context, Arrays.asList("value>150"), "") == null);
  }

  @Test
  public void testNumberCompString4() {
    RuleRunner runner = QlExpressRunner.getInstance();
    Map<String, Object> context = new HashMap<>();
    context.put("value", "<0.1234ng/ml");
    Assert.assertTrue((Boolean) runner.executeExpression(context, Arrays.asList("value<0.3"), ""));
  }

  @Test
  public void testNormal() {
    RuleRunner runner = QlExpressRunner.getInstance();
    Map<String, Object> context = new HashMap<>();
    context.put("value", 160);
    Assert.assertTrue(
        (Boolean) runner.executeExpression(context, Arrays.asList("value > 150"), ""));
  }

  @Test
  public void testCombination() {
    RuleRunner runner = QlExpressRunner.getInstance();
    Map<String, Object> context = new HashMap<>();
    context.put("value", "高");
    Assert.assertTrue(
        (Boolean)
            runner.executeExpression(context, Arrays.asList("value > 150 || value == '高'"), ""));
  }
}
