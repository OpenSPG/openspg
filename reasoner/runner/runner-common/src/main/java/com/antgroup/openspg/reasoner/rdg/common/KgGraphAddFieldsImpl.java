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

package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.exception.IllegalArgumentException;
import com.antgroup.openspg.reasoner.common.graph.type.GraphItemType;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.pattern.PartialGraphPattern;
import com.antgroup.openspg.reasoner.lube.logical.EdgeVar;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.PropertyVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.antgroup.openspg.reasoner.util.KgGraphSchema;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.*;
import lombok.Builder;
import lombok.Data;
import scala.Tuple2;
import scala.collection.JavaConversions;

public class KgGraphAddFieldsImpl implements Serializable {
  private static final long serialVersionUID = -4500069867630323486L;
  private final PartialGraphPattern kgGraphSchema;
  private final Map<String, Object> initRuleContext;
  private final KgGraphSplitStaticParameters staticParameters;
  private final long version;
  private final String taskId;
  private final List<AddFieldsInfo> addFieldsInfoList = new ArrayList<>();

  /** add fields implement */
  public KgGraphAddFieldsImpl(
      Map<Var, List<String>> addFieldsInfo,
      PartialGraphPattern kgGraphSchema,
      long version,
      String taskId) {
    this.taskId = taskId;
    this.kgGraphSchema = kgGraphSchema;
    this.initRuleContext = RunnerUtil.getKgGraphInitContext(this.kgGraphSchema);
    this.staticParameters = new KgGraphSplitStaticParameters(null, this.kgGraphSchema);
    this.version = version;
    Map<String, GraphItemType> alias2TypeMap =
        new HashMap<>(JavaConversions.mapAsJavaMap(KgGraphSchema.alias2Type(this.kgGraphSchema)));

    for (Map.Entry<Var, List<String>> entry : addFieldsInfo.entrySet()) {
      Var field = entry.getKey();
      String alias;
      List<String> propertyNameList = new ArrayList<>();

      if (field instanceof NodeVar) {
        NodeVar nodeField = (NodeVar) field;
        alias = nodeField.name();
        JavaConversions.setAsJavaSet(nodeField.fields())
            .forEach(f -> propertyNameList.add(f.name()));
      } else if (field instanceof EdgeVar) {
        EdgeVar edgeField = (EdgeVar) field;
        alias = edgeField.name();
        JavaConversions.setAsJavaSet(edgeField.fields())
            .forEach(f -> propertyNameList.add(f.name()));
      } else if (field instanceof PropertyVar) {
        PropertyVar propertyVar = (PropertyVar) field;
        alias = propertyVar.name();
        propertyNameList.add(propertyVar.field().name());
      } else {
        throw new IllegalArgumentException(
            "NodeVar or EdgeVar", field.getClass().getName(), "", null);
      }
      this.addFieldsInfoList.add(
          AddFieldsInfo.builder()
              .alias(alias)
              .type(alias2TypeMap.get(alias))
              .propertyNameList(propertyNameList)
              .expressionList(entry.getValue())
              .build());
    }
  }

  /** implement */
  public List<KgGraph<IVertexId>> map(KgGraph<IVertexId> value) {
    List<KgGraph<IVertexId>> result = new ArrayList<>();
    Iterator<KgGraph<IVertexId>> it = value.getPath(staticParameters, null);
    while (it.hasNext()) {
      KgGraph<IVertexId> path = it.next();
      if (path == null) {
        continue;
      }
      Map<String, Object> context = RunnerUtil.kgGraph2Context(this.initRuleContext, path);
      Map<Tuple2<String, GraphItemType>, Map<String, Object>> alias2UpdatePropertyMap =
          new HashMap<>();
      for (AddFieldsInfo addFieldsInfo : this.addFieldsInfoList) {
        Tuple2<String, GraphItemType> key =
            new Tuple2<>(addFieldsInfo.getAlias(), addFieldsInfo.getType());
        Object expressionResult = getFieldValue(addFieldsInfo, context, path);
        Map<String, Object> propertyMap =
            alias2UpdatePropertyMap.computeIfAbsent(key, k -> new HashMap<>());
        for (String propertyName : addFieldsInfo.getPropertyNameList()) {
          propertyMap.put(propertyName, expressionResult);
        }
      }

      for (Map.Entry<Tuple2<String, GraphItemType>, Map<String, Object>> entry :
          alias2UpdatePropertyMap.entrySet()) {
        String alias = entry.getKey()._1();
        GraphItemType type = entry.getKey()._2();
        Map<String, Object> propertyMap = entry.getValue();
        if (GraphItemType.VERTEX.equals(type)) {
          path.setVertexProperty(alias, propertyMap, version);
        } else {
          path.setEdgeProperty(alias, propertyMap);
        }
      }
      result.add(path);
    }
    return result;
  }

  private Object getFieldValue(
      AddFieldsInfo addFieldsInfo, Map<String, Object> context, KgGraph<IVertexId> path) {
    if (1 == addFieldsInfo.getExpressionList().size()) {
      String expressionString = addFieldsInfo.getExpressionList().get(0);
      if (expressionString.endsWith(Constants.PROPERTY_JSON_KEY)
          || expressionString.endsWith(Constants.GET_PATH_KEY)) {
        List<String> getPropertyList = Lists.newArrayList(Splitter.on(".").split(expressionString));
        return SelectRowImpl.getSelectValue(
            getPropertyList.get(0), getPropertyList.get(1), context);
      }
      if (expressionString.endsWith(Constants.EDGE_SET_KEY)) {
        List<Object> edges = new ArrayList<>();
        for (String e : path.getEdgeAlias()) {
          edges.add(RunnerUtil.recoverContextKeys(context.get(e)));
        }
        return edges;
      }
    }
    return RuleRunner.getInstance()
        .executeExpression(context, addFieldsInfo.getExpressionList(), this.taskId);
  }

  @Data
  @Builder
  private static class AddFieldsInfo implements Serializable {
    private String alias;
    private GraphItemType type;
    private List<String> propertyNameList;
    private List<String> expressionList;
  }
}
