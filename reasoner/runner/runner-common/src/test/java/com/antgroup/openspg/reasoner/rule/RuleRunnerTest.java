/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.reasoner.rule;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Expr2QlexpressTransformer;
import com.antgroup.openspg.reasoner.parser.expr.RuleExprParser;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.JavaConversions;

public class RuleRunnerTest {

  /** test overloading */
  @Test
  public void testOverloading() {
    Map<String, Object> context = new HashMap<>();
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R1 = concat('1', '2') == '12'",
                    "R2 = concat('x', 'y', 'z', 1, 2, 3) == 'xyz123'",
                    "R3 = current_time_millis() / 1000 - unix_timestamp() < 10",
                    "R1 && R2 && R3"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testArrayResult() {
    Map<String, Object> context = new HashMap<>();
    Object rst =
        RuleRunner.getInstance()
            .executeExpression(context, Lists.newArrayList("['abc', 'cdf']"), "");
    System.out.println(JSON.toJSONString(rst));
    Assert.assertTrue(rst instanceof String[]);
    rst = RuleRunner.getInstance().executeExpression(context, Lists.newArrayList("'abcdef'"), "");
    System.out.println(JSON.toJSONString(rst));
  }

  @Test
  public void testRLike() {
    Map<String, Object> context = new HashMap<>();
    context.put(
        "a",
        new HashMap<String, String>() {
          {
            put("phone", "1345-23456");
          }
        });
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "a.phone rlike '(63-)|(60-)|(66-)|(81-)|(84-)|(852-)|(855-)|(91-)|(95-)|(62-)|(853-)|(856-)|(886-)|(1345-)'"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testStrUDF() {
    Map<String, Object> context = new HashMap<>();
    context.put(
        "InsurancePeriod",
        new HashMap<String, String>() {
          {
            put("value", "70岁");
          }
        });
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "str_contains(InsurancePeriod.value,'岁') && cast_type(sub_str(InsurancePeriod.value,1,in_str(InsurancePeriod.value,'岁') - 1),'int') >= 70 || str_contains(InsurancePeriod.value,'年') && cast_type(sub_str(InsurancePeriod.value,1,in_str(InsurancePeriod.value,'年') - 1),'int') > 200"),
                "");
    Assert.assertTrue(rst);
  }

  /** test context */
  @Test
  public void testContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("A", 1000);
    context.put("B", 100);
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R1 = A + B == 1100",
                    "R2 = A > B",
                    "R3 = A >= B",
                    "R4 = B < A",
                    "R5 = B <= A",
                    "R6 = A != B",
                    "R1 && R2 && R3 && R4 && R5 && R6"),
                "");
    Assert.assertTrue(rst);
  }

  /** test context loss */
  @Test
  public void testContextLoss() {
    Map<String, Object> context = new HashMap<>();
    context.put("A", 1000);
    boolean rst = RuleRunner.getInstance().check(context, Lists.newArrayList("A > B"), "");
    Assert.assertFalse(rst);
  }

  @Test
  public void testConsts() {
    RuleExprParser parser = new RuleExprParser();

    Expr expr = parser.parse("\"\"");
    Expr2QlexpressTransformer transformer = new Expr2QlexpressTransformer();
    List<String> rules =
        Lists.newArrayList(JavaConversions.asJavaCollection(transformer.transform(expr)));
    Map<String, Object> context = new HashMap<>();
    Object rst = RuleRunner.getInstance().executeExpression(context, rules, "");
    Assert.assertEquals(rst, "");
  }

  /** test context loss */
  @Test
  public void testContextLoss2() {
    Map<String, Object> context = new HashMap<>();
    Map<String, Object> property = new HashMap<>();
    context.put("A", property);
    property.put("id", "A1");
    property.put("name", "A1Name");
    property.put("age", 18);
    property.put("amt", 22.3);
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R1 = A.id == 'A1'",
                    "R2 = A.age == 18",
                    "R3 = A.notExist == null",
                    "R1 && R2 && R3"),
                "");
    Assert.assertTrue(rst);

    rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R1 = A.id == 'A1'",
                    "R2 = A.age == 18",
                    "R3 = A.notExist == '123'",
                    "R1 && R2 && R3"),
                "");
    Assert.assertFalse(rst);
  }

  /** test In op */
  @Test
  public void testIn() {
    Map<String, Object> context = new HashMap<>();
    Map<String, Object> propertyA = new HashMap<>();
    context.put("A", propertyA);
    propertyA.put("id", "A1");
    Map<String, Object> propertyB = new HashMap<>();
    context.put("B", propertyB);
    propertyB.put("idList", Lists.newArrayList("A1", "C1"));

    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R1 = 'str' in ['str']",
                    "R2 = 'str' in 'str22'",
                    "R3 = A.id in 'A1_suffix'",
                    "R4 = A.id in ['A1', 'xx']",
                    "R5 = A.id in B.idList",
                    "R6 = 12 in 12",
                    "R7 = 12.0 in [12.0, 13.0]",
                    "R1 && R2 && R3 && R4 && R5 && R6 && R7"),
                "");
    Assert.assertTrue(rst);
  }

  /** test concat have a lot of parameters */
  @Test
  public void testUdfQuery() {
    boolean rst =
        RuleRunner.getInstance()
            .check(
                new HashMap<>(),
                Lists.newArrayList("'0123456789' == concat(0,1,2,3,4,5,6,7,8,9)"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testMax() {
    Map<String, Object> context = new HashMap<>();
    context.put("list", Lists.newArrayList(1, 2, 3, 4, 5));
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList("R1 = max(list) == 5", "R2 = min(list) == 1", "R1 && R2"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testHourOfDay() {
    Map<String, Object> context = new HashMap<>();
    Map<String, Object> t = new HashMap<>();
    t.put("payDate", 1691950562L);
    context.put("t", t);
    boolean rst =
        RuleRunner.getInstance()
            .check(context, Lists.newArrayList("hourOfDay(t.payDate * 1000) < 10"), "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testProject() {
    boolean rst =
        RuleRunner.getInstance()
            .check(
                new HashMap<>(), Lists.newArrayList("R1 = 1 == 1", "R2 = 10 > 9", "R1 && R2"), "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testRuleValue() {
    boolean rst =
        RuleRunner.getInstance()
            .check(
                new HashMap<>(),
                Lists.newArrayList(
                    "R1 = rule_value(true, 1, 2) == 1",
                    "R2 = rule_value(false, 1, 2) == 2",
                    "R1 && R2 && rule_value(false, 1, rule_value(true, 'x', 'y')) == 'x'"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testRuleCombine2() {
    Map<String, Object> context = new HashMap<>();
    context.put("sex", "男");
    context.put("MonthAmount", 3000);
    context.put("DayliyAmount", 301);
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R1 = sex == '男'",
                    "R3 = DayliyAmount > 300",
                    "R4 = MonthAmount < 500",
                    "R3 && R1 && !(R4 && R1)"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testRuleArrayParameter() {
    Map<String, Object> context = new HashMap<>();
    context.put("dateArray", new Date[] {new Date(0), new Date(1000)});
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R1 = \"男女\" == concat([\"男\", \"女\"])",
                    "R2 = \"123\" == concat([1, 2, 3])",
                    "R3 = \"123\" == concat([1L, 2L, 3L])",
                    "R4 = \"\" == concat(dateArray)",
                    "R1 && R2 && R3 && !R4"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testRuleListParameter() {
    Map<String, Object> context = new HashMap<>();
    context.put("dateList", Lists.newArrayList(new Date(), new Date(1000)));
    context.put("charList", Lists.newArrayList('a', 'b'));
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R1 = \"\" == concat([])",
                    "R2 = \"abc\" == concat(['a', 'b', 'c'])",
                    "R3 = \"1.02.0\" == concat([1.0, 2.0])",
                    "R4 = \"truefalse\" == concat([true, false])",
                    "R5 = \"\" == concat(dateList)",
                    "R6 = \"ab\" == concat(charList)",
                    "R1 && R2 && R3 && R4 && !R5 && R6"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testStrRules() {
    Map<String, Object> context = new HashMap<>();
    context.put("strList", Lists.newArrayList("a", "b", "c"));
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R1 = concat_ws(',', 1, 2) == '1,2'",
                    "R2 = hash(1) == 1",
                    "R3 = is_blank('')",
                    "R4 = contains_tag('1,2,3,4', '4,5,6,7', ',')",
                    "R5 = 7 == str_length('1,2,3,4')",
                    "R6 = concat_ws('|', strList) == 'a|b|c'",
                    "R7 = str_length(null) == 0",
                    "R1 && R2 && R3 && R4 && R5 && R6 && R7"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testContainsAny2() {
    Map<String, Object> context = new HashMap<>();
    context.put("l", Lists.newArrayList("a", "b", "c"));
    context.put(
        "s",
        new HashMap<String, String>() {
          {
            put("entity", "影像学检查");
          }
        });
    List<String> rules =
        Lists.newArrayList(
            "rule_value(contains_any(s.entity,[\"包膜\"]) && contains_any(s.inspection,[\"MRI\",\"CT\",\"PETCT\",\"PETMRI\"]) && contains_any(s.index,[\"累及\",\"侵犯\",\"膨隆\",\"突破包膜\"]) || contains_any(s.entity,\"神经血管束\",\"血管神经束\",\"DVC\") && contains_any(s.inspection,[\"MRI\",\"PETMRI\"]) && contains_any(s.index,[\"累及\",\"侵犯\"]),\"TRUE\",rule_value(contains_any(s.entity,\"包膜\") && contains_any(s.inspection,[\"MRI\",\"CT\",\"PETCT\",\"PETMRI\"]) && contains_any(s.index,[\"完整\"]) || contains_any(s.entity,\"神经血管束\",\"血管神经束\",\"DVC\") && contains_any(s.inspection,[\"MRI\",\"PETMRI\"]) && contains_any(s.index,[\"完整\"]),\"FALSE\",\"\"))");
    boolean rst = RuleRunner.getInstance().check(context, rules, "");
    Assert.assertFalse(rst);
  }

  @Test
  public void testContainsAny() {
    RuleExprParser ruleExprParser = new RuleExprParser();
    Expr e =
        ruleExprParser.parse(
            "contains_any(s.entity, [\"包膜\"]) "
                + "and contains_any(s.inspection, [\"MRI\", \"CT\", \"PETCT\", \"PETMRI\"]) "
                + "and contains_any(s.status, [\"完整\"]) "
                + "and "
                + "not contains_any(s.status, [\"无\", \"不\", \"未见\"])");

    Expr2QlexpressTransformer transformer = new Expr2QlexpressTransformer();

    List<String> rules =
        Lists.newArrayList(JavaConversions.asJavaCollection(transformer.transform(e)));
    Map<String, Object> context = new HashMap<>();
    context.put("l", Lists.newArrayList("a", "b", "c"));
    context.put(
        "s",
        new HashMap<String, String>() {
          {
            put("entity", "包膜");
            put("inspection", "MRI");
            put("status", "完整");
          }
        });

    boolean rst = RuleRunner.getInstance().check(context, rules, "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testContainsAny3() {
    RuleExprParser ruleExprParser = new RuleExprParser();
    Expr e = ruleExprParser.parse("contains_any(s.entity, '包膜') and contains_any(s.entity, a)");

    Expr2QlexpressTransformer transformer = new Expr2QlexpressTransformer();

    List<String> rules =
        Lists.newArrayList(JavaConversions.asJavaCollection(transformer.transform(e)));
    Map<String, Object> context = new HashMap<>();
    context.put("l", Lists.newArrayList("a", "b", "c"));
    context.put(
        "s",
        new HashMap<String, String>() {
          {
            put("entity", "包膜");
            put("inspection", "MRI");
            put("status", "完整");
          }
        });

    boolean rst = RuleRunner.getInstance().check(context, rules, "");
    Assert.assertFalse(rst);
  }
}
