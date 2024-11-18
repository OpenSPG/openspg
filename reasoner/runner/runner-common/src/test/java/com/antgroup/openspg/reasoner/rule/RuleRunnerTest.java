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

package com.antgroup.openspg.reasoner.rule;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.PathEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.graph.IRField;
import com.antgroup.openspg.reasoner.lube.utils.ExprUtils;
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Expr2QlexpressTransformer;
import com.antgroup.openspg.reasoner.parser.expr.RuleExprParser;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.antgroup.openspg.reasoner.udf.utils.DateUtils;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scala.collection.JavaConversions;

public class RuleRunnerTest {
  private RuleExprParser ruleExprParser;

  @Before
  public void init() {
    DateUtils.timeZone = TimeZone.getTimeZone("Asia/Shanghai");
    ruleExprParser = new RuleExprParser();
  }

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
    context.put("A/B*C", 200);
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "R0 = get_value(\"A/B*C\") == 200 && get_value() == null && get_value(\"B\") == 100",
                    "R1 = A + B == 1100",
                    "R2 = A > B",
                    "R3 = A >= B",
                    "R4 = B < A",
                    "R5 = B <= A",
                    "R6 = A != B",
                    "R0 && R1 && R2 && R3 && R4 && R5 && R6"),
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
    Expr2QlexpressTransformer transformer =
        new Expr2QlexpressTransformer(RuleRunner::convertPropertyName);
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

    Expr2QlexpressTransformer transformer =
        new Expr2QlexpressTransformer(RuleRunner::convertPropertyName);

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

    Expr2QlexpressTransformer transformer =
        new Expr2QlexpressTransformer(RuleRunner::convertPropertyName);

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

  @Test
  public void testKeywordConvert() {
    RuleExprParser ruleExprParser = new RuleExprParser();
    Expr e = ruleExprParser.parse("A.alias == B.alias && A.when == B.id");

    Expr2QlexpressTransformer transformer =
        new Expr2QlexpressTransformer(RuleRunner::convertPropertyName);

    List<String> rules =
        Lists.newArrayList(JavaConversions.asJavaCollection(transformer.transform(e)));
    Map<String, Object> context = new HashMap<>();
    context.put(
        "A",
        new HashMap<String, String>() {
          {
            put(RuleRunner.convertPropertyName("alias"), "alias_value");
            put(RuleRunner.convertPropertyName("when"), "id_value");
          }
        });

    context.put(
        "B",
        new HashMap<String, String>() {
          {
            put(RuleRunner.convertPropertyName("alias"), "alias_value");
            put(RuleRunner.convertPropertyName("id"), "id_value");
          }
        });

    boolean rst = RuleRunner.getInstance().check(context, rules, "");
    Assert.assertTrue(rst);
  }

  private Map<String, Object> getRepeatTestContext() {
    Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
    alias2VertexMap.put(
        "A",
        Sets.newHashSet(
            new Vertex<>(
                IVertexId.from(0L, "v"),
                new VertexVersionProperty("age", 18, "gender", "男", "logId", "xx1"))));
    alias2VertexMap.put(
        "B",
        Sets.newHashSet(
            new Vertex<>(
                IVertexId.from(2L, "v"),
                new VertexVersionProperty("age", 20, "gender", "男", "logId", "xx1"))));
    Map<String, Object> edge1Property = new HashMap<>();
    edge1Property.put("rate", 0.5);
    Map<String, Object> edge2Property = new HashMap<>();
    edge2Property.put("rate", 0.3);
    PathEdge<IVertexId, IProperty, IProperty> pathEdge1 =
        new PathEdge<>(
            new Edge<>(
                IVertexId.from(0L, "v"),
                IVertexId.from(1L, "v"),
                new EdgeProperty(edge1Property),
                0L,
                Direction.OUT,
                "e"));
    PathEdge<IVertexId, IProperty, IProperty> pathEdge2 =
        new PathEdge<>(
            pathEdge1,
            new Vertex<>(
                IVertexId.from(1L, "v"),
                new VertexVersionProperty("age", 19, "gender", "男", "logId", "xx1")),
            new Edge<>(
                IVertexId.from(1L, "v"),
                IVertexId.from(2L, "v"),
                new EdgeProperty(edge2Property),
                0L,
                Direction.OUT,
                "e"));
    Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
    alias2EdgeMap.put("e", Sets.newHashSet(pathEdge2));
    KgGraph<IVertexId> kgGraph = new KgGraphImpl(alias2VertexMap, alias2EdgeMap);
    return RunnerUtil.kgGraph2Context(new HashMap<>(), kgGraph);
  }

  @Test
  public void testRepeatReduce() {
    Map<String, Object> context = getRepeatTestContext();
    Object rst =
        RuleRunner.getInstance()
            .executeExpression(
                context,
                Lists.newArrayList("repeat_reduce(e.edges, 1, 'pre', 'cur', 'cur.rate * pre')"),
                "");
    Assert.assertEquals(rst, 0.15);
  }

  @Test
  public void testRepeatConstraint() {
    Map<String, Object> context = getRepeatTestContext();
    boolean rst =
        RuleRunner.getInstance()
            .check(
                context,
                Lists.newArrayList(
                    "repeat_constraint(e.nodes, 'pre', 'cur', 'cur.logId == pre.logId && cur.age >= A.age && cur.gender == B.gender', "
                        + "context_capturer(['A.age', 'B.gender'], [A.age, B.gender]))"),
                "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testRepeatConstraint2() {
    Expr e =
        ruleExprParser.parse(
            "e.nodes().constraint((pre,cur) => cur.logId == pre.logId && cur.age >= A.age && cur.gender == B.gender)");
    List<IRField> l =
        JavaConversions.seqAsJavaList(
            ExprUtils.getAllInputFieldInRule(
                e,
                Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("B", "A")),
                Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("e"))));
    Set<String> aliasSet = l.stream().map(IRField::name).collect(Collectors.toSet());
    Assert.assertEquals(3, l.size());
    Assert.assertTrue(aliasSet.contains("A"));
    Assert.assertTrue(aliasSet.contains("B"));
    Assert.assertTrue(aliasSet.contains("e"));
    Expr2QlexpressTransformer transformer =
        new Expr2QlexpressTransformer(RuleRunner::convertPropertyName);
    List<String> rules =
        Lists.newArrayList(JavaConversions.asJavaCollection(transformer.transform(e)));
    Assert.assertEquals(
        rules.get(0),
        "repeat_constraint(e.nodes, \"pre\", \"cur\", '((cur.logId == pre.logId) && (cur.age >= A.age)) && (cur.gender == B.gender)', "
            + "context_capturer([\"A.age\",\"B.gender\"],[A.age,B.gender]))");
    Map<String, Object> context = getRepeatTestContext();
    boolean rst = RuleRunner.getInstance().check(context, rules, "");
    Assert.assertTrue(rst);
  }

  @Test
  public void testRepeatReduce2() {
    Expr e = ruleExprParser.parse("e.edges().reduce((pre, cur) => cur.rate * pre, 1)");
    Expr2QlexpressTransformer transformer =
        new Expr2QlexpressTransformer(RuleRunner::convertPropertyName);
    List<String> rules =
        Lists.newArrayList(JavaConversions.asJavaCollection(transformer.transform(e)));
    Assert.assertEquals(rules.get(0), "repeat_reduce(e.edges, 1, 'pre', 'cur', 'cur.rate * pre')");
    Map<String, Object> context = getRepeatTestContext();
    Object rst = RuleRunner.getInstance().executeExpression(context, rules, "");
    Assert.assertEquals(rst, 0.15);
  }

  @Test
  public void testRepeatReduce3() {
    Expr e =
        ruleExprParser.parse(
            "e.nodes().reduce((res, ele) => concat(res, \"#\", Cast(ele.age - A.age, 'String')), '')");
    Expr2QlexpressTransformer transformer =
        new Expr2QlexpressTransformer(RuleRunner::convertPropertyName);
    List<String> rules =
        Lists.newArrayList(JavaConversions.asJavaCollection(transformer.transform(e)));
    Assert.assertEquals(
        "repeat_reduce(e.nodes, \"\", 'res', 'ele', 'concat(res,\"#\",cast_type(ele.age - A.age,\"String\"))', context_capturer([\"A.age\"],[A.age]))",
        rules.get(0));
    Map<String, Object> context = getRepeatTestContext();
    Object rst = RuleRunner.getInstance().executeExpression(context, rules, "");
    Assert.assertEquals(rst, "#0#1#2");
  }
}
