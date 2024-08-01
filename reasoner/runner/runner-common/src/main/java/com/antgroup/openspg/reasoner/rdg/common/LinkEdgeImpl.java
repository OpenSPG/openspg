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
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.VertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.EdgePattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.LinkedPatternConnection;
import com.antgroup.openspg.reasoner.lube.common.pattern.PartialGraphPattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.PatternElement;
import com.antgroup.openspg.reasoner.udf.model.BaseUdtf;
import com.antgroup.openspg.reasoner.udf.model.LinkedUdtfResult;
import com.antgroup.openspg.reasoner.udf.model.UdtfMeta;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.antgroup.openspg.reasoner.warehouse.common.partition.BasePartitioner;
import com.antgroup.openspg.reasoner.warehouse.utils.WareHouseUtils;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scala.collection.JavaConversions;

public class LinkEdgeImpl implements Serializable {
  private final String taskId;
  private final KgGraphSplitStaticParameters staticParameters;

  private final EdgePattern<LinkedPatternConnection> linkedEdgePattern;
  private final UdtfMeta udtfMeta;
  private final BasePartitioner partitioner;
  private final transient GraphState<IVertexId> graphState;

  private final List<List<String>> allExprList;
  private final Map<String, Object> initContext;

  public LinkEdgeImpl(
      String taskId,
      PartialGraphPattern kgGraphSchema,
      KgGraphSplitStaticParameters staticParameters,
      EdgePattern<LinkedPatternConnection> linkedEdgePattern,
      UdtfMeta udtfMeta,
      BasePartitioner partitioner,
      GraphState<IVertexId> graphState) {
    this.taskId = taskId;
    this.staticParameters = staticParameters;
    this.linkedEdgePattern = linkedEdgePattern;
    this.udtfMeta = udtfMeta;
    this.partitioner = partitioner;
    this.graphState = graphState;

    allExprList = new ArrayList<>();
    List<Expr> exprList = JavaConversions.seqAsJavaList(linkedEdgePattern.edge().params());
    for (Expr expr : exprList) {
      List<String> exprStr = WareHouseUtils.getRuleList(expr);
      allExprList.add(exprStr);
    }

    this.initContext = RunnerUtil.getKgGraphInitContext(kgGraphSchema);
  }

  private boolean isTargetVertexTypeEmpty(List<String> targetVertexTypes) {
    if (null == targetVertexTypes || targetVertexTypes.isEmpty()) {
      return true;
    }
    for (String type : targetVertexTypes) {
      if ("RdfProperty".equals(type)) {
        continue;
      }
      if (StringUtils.isNotEmpty(type)) {
        return false;
      }
    }
    return true;
  }

  public List<KgGraph<IVertexId>> link(KgGraph<IVertexId> kgGraph) {
    Iterator<KgGraph<IVertexId>> it = kgGraph.getPath(staticParameters, null);
    List<KgGraph<IVertexId>> mergeList = new ArrayList<>();

    while (it.hasNext()) {
      KgGraph<IVertexId> path = it.next();
      Map<String, Object> context = RunnerUtil.kgGraph2Context(this.initContext, path);
      List<Object> paramList = new ArrayList<>();
      for (List<String> exprStr : allExprList) {
        Object parameter = RuleRunner.getInstance().executeExpression(context, exprStr, taskId);
        paramList.add(parameter);
      }

      String sourceAlias = linkedEdgePattern.src().alias();
      Set<String> targetTypeSet = JavaConversions.setAsJavaSet(linkedEdgePattern.dst().typeNames());
      BaseUdtf tableFunction = udtfMeta.createTableFunction();
      tableFunction.initialize(graphState, context, sourceAlias, targetTypeSet);
      tableFunction.process(paramList);
      List<List<Object>> udtfResult = tableFunction.getCollector();
      List<LinkedUdtfResult> linkedUdtfResultList =
          udtfResult.stream()
              .flatMap(List::stream)
              .filter(Objects::nonNull)
              .map(
                  obj -> {
                    if (!(obj instanceof LinkedUdtfResult)) {
                      throw new RuntimeException("linked udtf must return LinkedUdtfResult");
                    }
                    return ((LinkedUdtfResult) obj);
                  })
              .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(linkedUdtfResultList)) {
        continue;
      }
      List<IVertex<IVertexId, IProperty>> sourceList = path.getVertex(sourceAlias);
      if (null == sourceList || sourceList.size() != 1) {
        throw new RuntimeException("There is more than one start vertex in kgGraph path");
      }
      IVertex<IVertexId, IProperty> sourceVertex = sourceList.get(0);
      IVertexId sourceId = sourceVertex.getId();
      Connection pc = linkedEdgePattern.edge();

      Map<String, Set<IVertex<IVertexId, IProperty>>> newAliasVertexMap = new HashMap<>();
      Map<String, Set<IEdge<IVertexId, IProperty>>> newAliasEdgeMap = new HashMap<>();
      for (LinkedUdtfResult linkedUdtfResult : linkedUdtfResultList) {
        for (String targetIdStr : linkedUdtfResult.getTargetVertexIdList()) {
          // add target vertex
          PatternElement targetVertexMeta = linkedEdgePattern.dst();
          String targetAlias = targetVertexMeta.alias();
          List<String> targetVertexTypes =
              new ArrayList<>(JavaConversions.setAsJavaSet(targetVertexMeta.typeNames()));
          if (isTargetVertexTypeEmpty(targetVertexTypes)) {
            targetVertexTypes = Lists.newArrayList(linkedUdtfResult.getVertexType());
            if (isTargetVertexTypeEmpty(targetVertexTypes)) {
              throw new RuntimeException(
                  "Linked edge target vertex type must contains at least one type");
            }
          }
          for (String targetVertexType : targetVertexTypes) {
            IVertexId targetId = new VertexId(targetIdStr, targetVertexType);
            Map<String, Object> propertyMap = new HashMap<>();
            VertexProperty vertexProperty = new VertexProperty(propertyMap);
            vertexProperty.put(Constants.NODE_ID_KEY, targetIdStr);
            vertexProperty.put(Constants.CONTEXT_LABEL, targetVertexType);
            if (partitioner != null && !partitioner.canPartition(targetId)) {
              continue;
            }
            // need add property with id
            Set<IVertex<IVertexId, IProperty>> newVertexSet =
                newAliasVertexMap.computeIfAbsent(targetAlias, k -> new HashSet<>());
            newVertexSet.add(new Vertex<>(targetId, vertexProperty));

            Map<String, Object> props = new HashMap<>(linkedUdtfResult.getEdgePropertyMap());
            props.put(Constants.EDGE_TO_ID_KEY, targetIdStr);
            if (sourceVertex.getValue().isKeyExist(Constants.NODE_ID_KEY)) {
              props.put(
                  Constants.EDGE_FROM_ID_KEY, sourceVertex.getValue().get(Constants.NODE_ID_KEY));
            }
            IProperty property = new EdgeProperty(props);

            // construct new edge
            IEdge<IVertexId, IProperty> linkedEdge = new Edge<>(sourceId, targetId, property);
            String edgeType =
                StringUtils.isNotEmpty(linkedUdtfResult.getEdgeType())
                    ? linkedUdtfResult.getEdgeType()
                    : linkedEdgePattern.edge().funcName();
            linkedEdge.setType(sourceId.getType() + "_" + edgeType + "_" + targetVertexType);
            String edgeAlias = pc.alias();

            Set<IEdge<IVertexId, IProperty>> newEdgeSet =
                newAliasEdgeMap.computeIfAbsent(edgeAlias, k -> new HashSet<>());
            newEdgeSet.add(linkedEdge);
          }
        }
      }
      if (!(newAliasVertexMap.isEmpty() && newAliasEdgeMap.isEmpty())) {
        KgGraph<IVertexId> newKgGraph = new KgGraphImpl(newAliasVertexMap, newAliasEdgeMap);
        path.merge(Lists.newArrayList(newKgGraph), null);
        mergeList.add(path);
      }
    }
    return mergeList;
  }
}
