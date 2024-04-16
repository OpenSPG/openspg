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

package com.antgroup.openspg.reasoner.runner.local.impl;

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.MirrorVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.NoneVertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.logical.RepeatPathVar;
import com.antgroup.openspg.reasoner.lube.physical.PropertyGraph;
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Expr2QlexpressTransformer;
import com.antgroup.openspg.reasoner.recorder.EmptyRecorder;
import com.antgroup.openspg.reasoner.recorder.IExecutionRecorder;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.runner.local.rdg.LocalRDG;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.collections4.CollectionUtils;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.immutable.Set;

public class LocalPropertyGraph implements PropertyGraph<LocalRDG> {
  /** task info */
  private LocalReasonerTask task;

  /** start id from input */
  private List<Tuple2<String, String>> startIdTuple2List;

  /** graph state */
  private final GraphState<IVertexId> graphState;

  /** executor */
  private ThreadPoolExecutor threadPoolExecutor;

  /** executor timeout ms */
  private long executorTimeoutMs = 40 * 1000;

  /** default path limit */
  private long defaultPathLimit = 3000;

  /** carry traversal graph data */
  private boolean isCarryTraversalGraph = false;

  /** local property graph */
  public LocalPropertyGraph(GraphState<IVertexId> graphState) {
    this.graphState = graphState;
  }

  @Override
  public LocalRDG createRDG(String alias, Set<String> types) {
    LocalRDG result =
        new LocalRDG(
            graphState,
            getStartIdList(types),
            threadPoolExecutor,
            executorTimeoutMs,
            alias,
            getTaskId(),
            getExecutionRecorder(),
            isCarryTraversalGraph);
    result.setMaxPathLimit(getMaxPathLimit());
    result.setStrictMaxPathLimit(getStrictMaxPathLimit());
    return result;
  }

  @Override
  public LocalRDG createRDG(String alias, LocalRDG rdg) {
    java.util.Set<IVertexId> startIdSet = new HashSet<>();
    for (KgGraph<IVertexId> kgGraph : rdg.getKgGraphList()) {
      List<IVertex<IVertexId, IProperty>> vertexList = kgGraph.getVertex(alias);
      for (IVertex<IVertexId, IProperty> vertex : vertexList) {
        if (vertex instanceof NoneVertex) {
          if (vertex instanceof MirrorVertex) {
            startIdSet.add(vertex.getId());
          }
          continue;
        }
        startIdSet.add(vertex.getId());
      }
    }
    LocalRDG result =
        new LocalRDG(
            graphState,
            Lists.newArrayList(startIdSet),
            threadPoolExecutor,
            executorTimeoutMs,
            alias,
            getTaskId(),
            // subquery can not carry all graph
            getExecutionRecorder(),
            false);
    result.setMaxPathLimit(getMaxPathLimit());
    result.setStrictMaxPathLimit(getStrictMaxPathLimit());
    return result;
  }

  @Override
  public LocalRDG createRDG(String alias, Expr id, Set<String> types) {
    java.util.Set<IVertexId> startIdSet = new HashSet<>();
    Expr2QlexpressTransformer transformer =
        new Expr2QlexpressTransformer(RuleRunner::convertPropertyName);
    List<String> exprQlList =
        Lists.newArrayList(JavaConversions.seqAsJavaList(transformer.transform(id)));
    List<String> idStrList = new ArrayList<>();
    Object idObj = RuleRunner.getInstance().executeExpression(new HashMap<>(), exprQlList, "");
    if (idObj instanceof String) {
      idStrList.add(String.valueOf(idObj));
    } else if (idObj instanceof List) {
      List idOList = (List) idObj;
      for (Object ido : idOList) {
        idStrList.add(String.valueOf(ido));
      }
    } else if (idObj instanceof String[]) {
      String[] idArray = (String[]) idObj;
      idStrList.addAll(Lists.newArrayList(idArray));
    } else if (idObj instanceof Object[]) {
      Object[] idArray = (Object[]) idObj;
      for (Object idO : idArray) {
        idStrList.add(String.valueOf(idO));
      }
    }
    for (String type : JavaConversions.asJavaCollection(types)) {
      for (String idStr : idStrList) {
        startIdSet.add(IVertexId.from(idStr, type));
      }
    }
    if (startIdSet.isEmpty()) {
      throw new RuntimeException("can not extract start id list");
    }
    LocalRDG result =
        new LocalRDG(
            graphState,
            Lists.newArrayList(startIdSet),
            threadPoolExecutor,
            executorTimeoutMs,
            alias,
            getTaskId(),
            // subquery can not carry all graph
            getExecutionRecorder(),
            false);
    result.setMaxPathLimit(getMaxPathLimit());
    result.setStrictMaxPathLimit(getStrictMaxPathLimit());
    return result;
  }

  @Override
  public LocalRDG createRDGFromPath(RepeatPathVar repeatVar, String alias, LocalRDG rdg) {
    return null;
  }

  public GraphState<IVertexId> getGraphState() {
    return graphState;
  }

  private String getTaskId() {
    if (task == null) {
      return "";
    }
    return task.getId();
  }

  private List<IVertexId> getStartIdList(Set<String> types) {
    if (CollectionUtils.isNotEmpty(this.startIdTuple2List)) {
      List<IVertexId> startIdList = new ArrayList<>();
      for (Tuple2<String, String> tuple2 : this.startIdTuple2List) {
        startIdList.add(IVertexId.from(tuple2._1(), tuple2._2()));
      }
      return startIdList;
    }
    List<IVertexId> startIdList = new ArrayList<>();
    Iterator<IVertex<IVertexId, IProperty>> it =
        this.graphState.getVertexIterator(JavaConversions.setAsJavaSet(types));
    while (it.hasNext()) {
      IVertex<IVertexId, IProperty> vertex = it.next();
      startIdList.add(vertex.getId());
    }
    return startIdList;
  }

  /**
   * Setter method for property <tt>startIdList</tt>.
   *
   * @param startIdTuple2List value to be assigned to property startIdList
   */
  public void setStartIdTuple2List(List<Tuple2<String, String>> startIdTuple2List) {
    this.startIdTuple2List = startIdTuple2List;
  }

  /**
   * Setter method for property <tt>threadPoolExecutor</tt>.
   *
   * @param threadPoolExecutor value to be assigned to property threadPoolExecutor
   */
  public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
    this.threadPoolExecutor = threadPoolExecutor;
  }

  /**
   * Setter method for property <tt>timeoutMillSeconds</tt>.
   *
   * @param executorTimeoutMs value to be assigned to property timeoutMillSeconds
   */
  public void setExecutorTimeoutMs(long executorTimeoutMs) {
    this.executorTimeoutMs = executorTimeoutMs;
  }

  /**
   * Setter method for property <tt>isCarryTraversalGraph</tt>.
   *
   * @param carryTraversalGraph value to be assigned to property isCarryTraversalGraph
   */
  public void setCarryTraversalGraph(boolean carryTraversalGraph) {
    isCarryTraversalGraph = carryTraversalGraph;
  }

  /** max path limit */
  private Long getMaxPathLimit() {
    Object maxPathLimitObj = null;
    if (null != task && null != this.task.getParams()) {
      maxPathLimitObj = this.task.getParams().get(ConfigKey.KG_REASONER_MAX_PATH_LIMIT);
    }
    if (null == maxPathLimitObj) {
      return defaultPathLimit;
    }
    return 2 * Long.parseLong(String.valueOf(maxPathLimitObj));
  }

  /** return strict max path limit */
  private Long getStrictMaxPathLimit() {
    Object maxPathLimitObj = null;
    if (null != task && null != this.task.getParams()) {
      maxPathLimitObj = this.task.getParams().get(ConfigKey.KG_REASONER_STRICT_MAX_PATH_THRESHOLD);
    }
    if (null == maxPathLimitObj) {
      return null;
    }
    return Long.parseLong(String.valueOf(maxPathLimitObj));
  }

  private IExecutionRecorder getExecutionRecorder() {
    if (null == task) {
      return new EmptyRecorder();
    }
    return task.getExecutionRecorder();
  }

  /**
   * Setter method for property <tt>task</tt>.
   *
   * @param task value to be assigned to property task
   */
  public void setTask(LocalReasonerTask task) {
    this.task = task;
  }
}
