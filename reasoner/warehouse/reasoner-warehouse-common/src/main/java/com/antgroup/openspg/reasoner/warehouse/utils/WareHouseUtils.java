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
package com.antgroup.openspg.reasoner.warehouse.utils;

import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.graph.IRField;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.PatternElement;
import com.antgroup.openspg.reasoner.lube.common.rule.Rule;
import com.antgroup.openspg.reasoner.lube.utils.RuleUtils;
import com.antgroup.openspg.reasoner.lube.utils.transformer.ExprTransformer;
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Expr2QlexpressTransformer;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import scala.Option;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.immutable.Set;

public class WareHouseUtils {

  private static final ExprTransformer<String> EXPR_TRANSFORMER = new Expr2QlexpressTransformer();

  /**
   * get vertex rule string
   *
   * @param pattern
   * @return
   */
  public static List<String> getVertexRuleList(Pattern pattern) {
    List<String> vertexRuleList = new ArrayList<>();
    if (null != pattern.root().rule()) {
      vertexRuleList.addAll(
          JavaConversions.asJavaCollection(EXPR_TRANSFORMER.transform(pattern.root().rule())));
    }
    return vertexRuleList;
  }

  /** get pattern dst vertex rule string */
  public static Map<String, List<String>> getDstVertexRuleList(Pattern pattern) {
    Map<String, List<String>> result = new HashMap<>();
    String rootAlias = pattern.root().alias();
    Option<Set<Connection>> connectionSet = pattern.topology().get(rootAlias);
    if (connectionSet.isEmpty()) {
      return result;
    }
    for (Connection connection : JavaConversions.setAsJavaSet(connectionSet.get())) {
      String dstAlias = connection.target();
      if (rootAlias.equals(dstAlias)) {
        dstAlias = connection.source();
      }
      PatternElement patternElement = pattern.getNode(dstAlias);
      if (null != patternElement.rule()) {
        result.computeIfAbsent(
            dstAlias,
            k ->
                Lists.newArrayList(
                    JavaConversions.asJavaCollection(
                        EXPR_TRANSFORMER.transform(patternElement.rule()))));
      }
    }
    return result;
  }

  /**
   * get edge rule string for each pattern connection
   *
   * @param pattern
   * @return
   */
  public static Map<Connection, List<String>> getEdgeRuleMap(Pattern pattern) {
    Map<Connection, List<String>> edgeRuleMap = new HashMap<>();
    Option<Set<Connection>> patternConnectionSet = pattern.topology().get(pattern.root().alias());
    if (!patternConnectionSet.isEmpty()) {
      for (Connection patternConnection :
          JavaConversions.setAsJavaSet(patternConnectionSet.get())) {
        if (null == patternConnection.rule()) {
          continue;
        }
        List<String> edgeRuleList =
            edgeRuleMap.computeIfAbsent(patternConnection, k -> new ArrayList<>());
        edgeRuleList.addAll(
            JavaConversions.asJavaCollection(EXPR_TRANSFORMER.transform(patternConnection.rule())));
      }
    }
    return edgeRuleMap;
  }

  /**
   * get edge rule list for each edge type
   *
   * @param pattern
   * @return
   */
  public static Map<String, List<Rule>> getEdgeTypeRuleMap(Pattern pattern) {
    Map<String, List<Rule>> edgeTypeRuleListMap = new HashMap<>();
    Option<Set<Connection>> patternConnectionSet = pattern.topology().get(pattern.root().alias());
    if (!patternConnectionSet.isEmpty()) {
      for (Connection patternConnection :
          JavaConversions.setAsJavaSet(patternConnectionSet.get())) {
        if (null == patternConnection.rule()) {
          continue;
        }
        for (String edgeType : JavaConversions.setAsJavaSet(patternConnection.relTypes())) {
          List<Rule> ruleList =
              edgeTypeRuleListMap.computeIfAbsent(edgeType, k -> new ArrayList<>());
          ruleList.add(patternConnection.rule());
        }
      }
    }
    return edgeTypeRuleListMap;
  }

  /** 直接转换成规则字符串 */
  public static List<String> getRuleList(Expr expr) {
    return Lists.newArrayList(JavaConversions.asJavaIterable(EXPR_TRANSFORMER.transform(expr)));
  }

  /** transform rule to qlexpress */
  public static List<String> getRuleList(Rule rule) {
    return Lists.newArrayList(JavaConversions.asJavaIterable(EXPR_TRANSFORMER.transform(rule)));
  }

  /** transform rule to qlexpress */
  public static Tuple2<String, List<String>> getRuleListWithAlias(Rule rule) {
    List<String> ruleList =
        Lists.newArrayList(JavaConversions.asJavaIterable(EXPR_TRANSFORMER.transform(rule)));
    List<IRField> irFieldList =
        JavaConversions.seqAsJavaList(RuleUtils.getAllInputFieldInRule(rule, null, null));
    if (irFieldList.size() > 1) {
      throw new RuntimeException("rule ir field size > 1, ruleList=" + ruleList);
    }
    return new Tuple2<>(irFieldList.get(0).name(), ruleList);
  }
}
