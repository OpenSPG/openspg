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

package com.antgroup.openspg.reasoner.runner.local.rdg;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexProperty;
import com.antgroup.openspg.reasoner.common.graph.type.GraphItemType;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.model.MergeTypeEnum;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.block.AddPredicate;
import com.antgroup.openspg.reasoner.lube.block.AddProperty;
import com.antgroup.openspg.reasoner.lube.block.AddVertex;
import com.antgroup.openspg.reasoner.lube.block.DDLOp;
import com.antgroup.openspg.reasoner.lube.block.SortItem;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.EdgePattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.LinkedPatternConnection;
import com.antgroup.openspg.reasoner.lube.common.pattern.NodePattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.PartialGraphPattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.PatternElement;
import com.antgroup.openspg.reasoner.lube.common.rule.Rule;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.PropertyVar;
import com.antgroup.openspg.reasoner.lube.logical.RichVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.lube.logical.planning.FullOuterJoin$;
import com.antgroup.openspg.reasoner.lube.logical.planning.InnerJoin$;
import com.antgroup.openspg.reasoner.lube.logical.planning.JoinType;
import com.antgroup.openspg.reasoner.lube.logical.planning.LeftOuterJoin$;
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG;
import com.antgroup.openspg.reasoner.lube.physical.rdg.Row;
import com.antgroup.openspg.reasoner.pattern.PatternMatcher;
import com.antgroup.openspg.reasoner.rdg.common.ExtractRelationImpl;
import com.antgroup.openspg.reasoner.rdg.common.ExtractVertexImpl;
import com.antgroup.openspg.reasoner.rdg.common.FoldEdgeImpl;
import com.antgroup.openspg.reasoner.rdg.common.FoldRepeatEdgeInfo;
import com.antgroup.openspg.reasoner.rdg.common.GroupByKeyItem;
import com.antgroup.openspg.reasoner.rdg.common.GroupByKgGraphImpl;
import com.antgroup.openspg.reasoner.rdg.common.KgGraphAddFieldsImpl;
import com.antgroup.openspg.reasoner.rdg.common.KgGraphAggregateImpl;
import com.antgroup.openspg.reasoner.rdg.common.KgGraphDropFieldsImpl;
import com.antgroup.openspg.reasoner.rdg.common.KgGraphEdgeDirectionImpl;
import com.antgroup.openspg.reasoner.rdg.common.KgGraphFirstEdgeAggImpl;
import com.antgroup.openspg.reasoner.rdg.common.KgGraphListProcess;
import com.antgroup.openspg.reasoner.rdg.common.KgGraphRenameImpl;
import com.antgroup.openspg.reasoner.rdg.common.KgGraphSortImpl;
import com.antgroup.openspg.reasoner.rdg.common.LinkEdgeImpl;
import com.antgroup.openspg.reasoner.rdg.common.ReasonerJoinImpl;
import com.antgroup.openspg.reasoner.rdg.common.SelectRowImpl;
import com.antgroup.openspg.reasoner.rdg.common.SinkRelationImpl;
import com.antgroup.openspg.reasoner.rdg.common.UnfoldEdgeImpl;
import com.antgroup.openspg.reasoner.rdg.common.UnfoldReduceDuplicateImpl;
import com.antgroup.openspg.reasoner.rdg.common.UnfoldRepeatEdgeInfo;
import com.antgroup.openspg.reasoner.rdg.common.model.JoinItem;
import com.antgroup.openspg.reasoner.recorder.EmptyRecorder;
import com.antgroup.openspg.reasoner.recorder.IExecutionRecorder;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.udf.model.UdtfMeta;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.antgroup.openspg.reasoner.util.KgGraphSchema;
import com.antgroup.openspg.reasoner.util.PathConnection;
import com.antgroup.openspg.reasoner.utils.PredicateKgGraph;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.antgroup.openspg.reasoner.warehouse.utils.WareHouseUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;

@Slf4j
public class LocalRDG extends RDG<LocalRDG> {
  /** graph state */
  private final GraphState<IVertexId> graphState;

  private final String startVertexAlias;

  /** kg graph and it's schema */
  private java.util.List<KgGraph<IVertexId>> kgGraphList = new ArrayList<>();

  private PartialGraphPattern kgGraphSchema = null;

  /** executor */
  private final ThreadPoolExecutor threadPoolExecutor;

  /** executor timeout ms */
  private final long executorTimeoutMs;

  /** max path limit */
  private Long maxPathLimit = null;

  /** strict path limit */
  private Long strictMaxPathLimit = null;

  /** rdg result list */
  private final java.util.Set<IVertex<IVertexId, IProperty>> resultVertexSet = new HashSet<>();

  private final java.util.Set<IEdge<IVertexId, IProperty>> resultEdgeSet = new HashSet<>();

  private final PatternMatcher patternMatcher;

  private final String taskId;

  private final IExecutionRecorder executionRecorder;

  /** local rdg with graph state */
  public LocalRDG(
      GraphState<IVertexId> graphState,
      java.util.List<IVertexId> startIdList,
      ThreadPoolExecutor threadPoolExecutor,
      long executorTimeoutMs,
      String startVertexAlias,
      String taskId,
      IExecutionRecorder executionRecorder) {
    this.graphState = graphState;
    Pattern startIdPattern = new NodePattern(new PatternElement(startVertexAlias, null, null));
    for (IVertexId vertexId : startIdList) {
      KgGraph<IVertexId> startId = new KgGraphImpl();
      startId.init(new Vertex<>(vertexId), null, startIdPattern);
      this.kgGraphList.add(startId);
    }
    this.threadPoolExecutor = threadPoolExecutor;
    this.executorTimeoutMs = executorTimeoutMs;
    this.startVertexAlias = startVertexAlias;
    this.taskId = taskId;
    this.patternMatcher = new PatternMatcher(this.taskId, graphState);

    if (null == executionRecorder) {
      this.executionRecorder = new EmptyRecorder();
    } else {
      this.executionRecorder = executionRecorder;
    }
    this.executionRecorder.entryRDG(startVertexAlias);
    this.executionRecorder.stageResult(
        "startId(" + startVertexAlias + ")", this.kgGraphList.size());
  }

  @Override
  public LocalRDG patternScan(Pattern pattern) {
    long startTime = System.currentTimeMillis();
    java.util.List<String> rootVertexRuleList = WareHouseUtils.getVertexRuleList(pattern);
    java.util.Map<String, java.util.List<String>> dstVertexRuleMap =
        WareHouseUtils.getDstVertexRuleList(pattern);
    java.util.Map<String, java.util.List<String>> edgeRuleMap =
        WareHouseUtils.getEdgeRuleMap(pattern);
    java.util.Map<String, java.util.List<Rule>> edgeTypeRuleMap =
        WareHouseUtils.getEdgeTypeRuleMap(pattern);
    log.info(
        "LocalRDG patternScan,"
            + ",pattern="
            + pattern
            + ",rule="
            + JSON.toJSONString(rootVertexRuleList)
            + ",edgeRule="
            + JSON.toJSONString(edgeRuleMap));

    long count = 0;
    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    java.util.List<CompletableFuture<KgGraph<IVertexId>>> futureList = new ArrayList<>();
    patternMatcher.resetInitTime();
    for (KgGraph<IVertexId> kgGraphId : this.kgGraphList) {
      IVertexId id = kgGraphId.getVertex(this.startVertexAlias).get(0).getId();
      CompletableFuture<KgGraph<IVertexId>> future =
          CompletableFuture.supplyAsync(
              new Supplier<KgGraph<IVertexId>>() {
                @Override
                public KgGraph<IVertexId> get() {
                  return patternMatcher.patternMatch(
                      id,
                      null,
                      null,
                      pattern,
                      rootVertexRuleList,
                      dstVertexRuleMap,
                      edgeRuleMap,
                      new HashMap<>(),
                      pattern.root().rule(),
                      edgeTypeRuleMap,
                      maxPathLimit,
                      true,
                      60 * 1000);
                }
              },
              threadPoolExecutor);
      futureList.add(future);
    }
    for (CompletableFuture<KgGraph<IVertexId>> future : futureList) {
      KgGraph<IVertexId> kgGraph;
      try {
        kgGraph = future.get(this.executorTimeoutMs, TimeUnit.MILLISECONDS);
      } catch (Exception e) {
        throw new RuntimeException("patternScan error " + e.getMessage(), e);
      }
      if (null == kgGraph) {
        continue;
      }
      if (Utils.randomLog()) {
        log.info(
            "LocalRDG PatternScan,kgGraph="
                + kgGraph.getVertex(pattern.root().alias()).get(0).getId());
      }
      count++;
      newKgGraphList.add(kgGraph);
      if (null != this.strictMaxPathLimit && newKgGraphList.size() > this.strictMaxPathLimit) {
        throw new RuntimeException("exceeding strict max path limit " + this.strictMaxPathLimit);
      }
    }
    this.kgGraphList = newKgGraphList;
    this.kgGraphSchema = KgGraphSchema.convert2KgGraphSchema(pattern);

    log.info(
        "LocalRDG patternScan,root="
            + pattern.root()
            + ",matchCount="
            + count
            + " cost time="
            + (System.currentTimeMillis() - startTime));
    this.executionRecorder.stageResultWithDesc(
        "patternScan(" + RunnerUtil.getReadablePattern(pattern) + ")",
        this.kgGraphList.size(),
        "SubPattern");
    return this;
  }

  @Override
  public LocalRDG linkedExpand(EdgePattern<LinkedPatternConnection> pattern) {
    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    UdtfMeta udtfMeta = RunnerUtil.chooseUdtfMeta(pattern);

    KgGraphSplitStaticParameters staticParameters =
        new KgGraphSplitStaticParameters(null, this.kgGraphSchema);

    long count = 0;
    long targetVertexSize = 0;
    LinkEdgeImpl linkEdge =
        new LinkEdgeImpl(
            this.taskId, this.kgGraphSchema, staticParameters, pattern, udtfMeta, null, graphState);
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      java.util.List<KgGraph<IVertexId>> splitedKgGraphList = linkEdge.link(kgGraph);
      if (CollectionUtils.isNotEmpty(splitedKgGraphList)) {
        KgGraph<IVertexId> result = new KgGraphImpl();
        result.merge(splitedKgGraphList, null);
        newKgGraphList.add(result);
        count++;
        targetVertexSize += splitedKgGraphList.size();
      }
    }
    this.kgGraphList = newKgGraphList;
    this.kgGraphSchema = KgGraphSchema.expandSchema(this.kgGraphSchema, pattern);

    log.info(
        "LinkedExpand, funcName="
            + pattern.edge().funcName()
            + ",matchCount="
            + count
            + ", linkedTargetVertexSize="
            + targetVertexSize);
    this.executionRecorder.stageResult(
        "linkedExpand(" + RunnerUtil.getReadablePattern(pattern) + ")", this.kgGraphList.size());
    return this;
  }

  private CompletableFuture<java.util.List<KgGraph<IVertexId>>> processKgGraphWithSameRoot(
      IVertexId rootId,
      java.util.List<KgGraph<IVertexId>> sameRootKgGraphList,
      java.util.Set<String> intersectionAliasSet,
      PartialGraphPattern matchPattern,
      java.util.List<String> vertexRuleList,
      java.util.Map<String, java.util.List<String>> dstVertexRuleMap,
      java.util.Map<String, java.util.List<String>> edgeRuleMap,
      java.util.Map<String, java.util.List<Rule>> edgeTypeRuleMap,
      PartialGraphPattern afterKgGraphSchema,
      ThreadPoolExecutor threadPoolExecutor) {

    PartialGraphPattern beforeKgGraphSchema = this.kgGraphSchema;
    return CompletableFuture.supplyAsync(
        new Supplier<java.util.List<KgGraph<IVertexId>>>() {
          @Override
          public java.util.List<KgGraph<IVertexId>> get() {
            if (null == rootId) {
              return null;
            }
            java.util.List<KgGraph<IVertexId>> result = new ArrayList<>();

            java.util.Map<String, java.util.Set<IVertexId>> edgeValidTargetIdSet = new HashMap<>();
            if (CollectionUtils.isNotEmpty(intersectionAliasSet)) {
              edgeValidTargetIdSet =
                  RunnerUtil.getEdgeAlias2ValidTargetIdMap(
                      intersectionAliasSet, sameRootKgGraphList, matchPattern);
            }

            KgGraph<IVertexId> matchedKgGraph =
                patternMatcher.patternMatch(
                    rootId,
                    null,
                    null,
                    matchPattern,
                    vertexRuleList,
                    dstVertexRuleMap,
                    edgeRuleMap,
                    edgeValidTargetIdSet,
                    matchPattern.root().rule(),
                    edgeTypeRuleMap,
                    maxPathLimit,
                    CollectionUtils.isEmpty(intersectionAliasSet),
                    60 * 1000);
            if (null == matchedKgGraph) {
              return null;
            }

            int matchCount =
                RunnerUtil.getMinVertexCount(matchedKgGraph, matchPattern.root().alias());

            long count = 0;
            for (KgGraph<IVertexId> kgGraph : sameRootKgGraphList) {
              // expend and prune by intersection vertex
              if (CollectionUtils.isEmpty(intersectionAliasSet)) {
                kgGraph.expand(matchedKgGraph, afterKgGraphSchema);
              } else {
                matchCount =
                    kgGraph.expandAndPrune(
                        beforeKgGraphSchema,
                        matchedKgGraph,
                        matchPattern,
                        afterKgGraphSchema,
                        intersectionAliasSet);
                if (matchCount <= 0) {
                  continue;
                }
              }
              if (kgGraph.checkDuplicateVertex()) {
                continue;
              }
              result.add(kgGraph);
              count += matchCount;
              if (null != maxPathLimit && count >= maxPathLimit) {
                log.warn(
                    "ExpandInto,pathLimit,count="
                        + count
                        + ",matchCount="
                        + matchCount
                        + ",id="
                        + rootId);
                break;
              }
            }
            return result;
          }
        },
        threadPoolExecutor);
  }

  @Override
  public LocalRDG expandInto(PatternElement target, Pattern pattern) {
    log.info("ExpandInto __max_path_size__ = " + maxPathLimit);
    long startTime = System.currentTimeMillis();

    String rootAlias = pattern.root().alias();
    shuffleAndGroup(rootAlias, false);

    java.util.List<String> vertexRuleList = WareHouseUtils.getVertexRuleList(pattern);
    java.util.Map<String, java.util.List<String>> dstVertexRuleMap =
        WareHouseUtils.getDstVertexRuleList(pattern);
    java.util.Map<String, java.util.List<String>> edgeRuleMap =
        WareHouseUtils.getEdgeRuleMap(pattern);
    java.util.Map<String, java.util.List<Rule>> edgeTypeRuleMap =
        WareHouseUtils.getEdgeTypeRuleMap(pattern);

    log.info(
        "LocalRDG expandInto,"
            + ",pattern="
            + pattern
            + ",rule="
            + JSON.toJSONString(vertexRuleList)
            + ",edgeRule="
            + JSON.toJSONString(edgeRuleMap));

    PartialGraphPattern afterKgGraphSchema =
        KgGraphSchema.expandSchema(
            this.kgGraphSchema, KgGraphSchema.convert2KgGraphSchema(pattern));
    PartialGraphPattern matchPattern = KgGraphSchema.convert2KgGraphSchema(pattern);
    java.util.Set<String> intersectionAliasSet =
        RunnerUtil.getIntersectionAliasSet(this.kgGraphSchema, matchPattern);

    java.util.List<CompletableFuture<java.util.List<KgGraph<IVertexId>>>> futureList =
        new ArrayList<>();

    java.util.List<KgGraph<IVertexId>> sameRootKgGraphList = new ArrayList<>();
    IVertexId lastVertexId = null;
    patternMatcher.resetInitTime();
    for (KgGraph<IVertexId> value : this.kgGraphList) {
      IVertex<IVertexId, IProperty> vertex = value.getVertex(matchPattern.root().alias()).get(0);
      if (vertex.getId().equals(lastVertexId)) {
        sameRootKgGraphList.add(value);
      } else {
        futureList.add(
            processKgGraphWithSameRoot(
                lastVertexId,
                sameRootKgGraphList,
                intersectionAliasSet,
                matchPattern,
                vertexRuleList,
                dstVertexRuleMap,
                edgeRuleMap,
                edgeTypeRuleMap,
                afterKgGraphSchema,
                threadPoolExecutor));
        sameRootKgGraphList = new ArrayList<>();
        sameRootKgGraphList.add(value);
        lastVertexId = vertex.getId();
      }
    }
    if (null != lastVertexId) {
      futureList.add(
          processKgGraphWithSameRoot(
              lastVertexId,
              sameRootKgGraphList,
              intersectionAliasSet,
              matchPattern,
              vertexRuleList,
              dstVertexRuleMap,
              edgeRuleMap,
              edgeTypeRuleMap,
              afterKgGraphSchema,
              threadPoolExecutor));
    }

    long count = 0;
    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    for (CompletableFuture<java.util.List<KgGraph<IVertexId>>> future : futureList) {
      java.util.List<KgGraph<IVertexId>> resultKgGraph;
      try {
        resultKgGraph = future.get(this.executorTimeoutMs, TimeUnit.MILLISECONDS);
      } catch (Exception e) {
        throw new RuntimeException("expandInto error", e);
      }

      if (null == resultKgGraph) {
        continue;
      }
      count += resultKgGraph.size();
      newKgGraphList.addAll(resultKgGraph);
      if (null != this.strictMaxPathLimit && newKgGraphList.size() > this.strictMaxPathLimit) {
        throw new RuntimeException("exceeding strict max path limit " + this.strictMaxPathLimit);
      }
    }
    this.kgGraphSchema = afterKgGraphSchema;
    this.kgGraphList = newKgGraphList;
    log.info(
        "LocalRDG ExpandInto,patternRoot="
            + pattern.root()
            + ",matchCount="
            + count
            + " cost time="
            + (System.currentTimeMillis() - startTime));
    this.executionRecorder.stageResult(
        "expandInto(" + RunnerUtil.getReadablePattern(pattern) + ")", this.kgGraphList.size());
    return this;
  }

  @Override
  public Row<LocalRDG> select(List<Var> cols, List<String> as) {
    java.util.List<String> crossBorderRuleList = new ArrayList<>();
    java.util.List<Var> columns = Lists.newArrayList(JavaConversions.asJavaCollection(cols));

    KgGraphSplitStaticParameters staticParameters =
        new KgGraphSplitStaticParameters(null, this.kgGraphSchema);

    SelectRowImpl impl = new SelectRowImpl(columns, kgGraphSchema, true);
    java.util.List<Object[]> rows = new ArrayList<>();
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      Predicate<KgGraph<IVertexId>> filter = null;
      if (CollectionUtils.isNotEmpty(crossBorderRuleList)) {
        filter = new PredicateKgGraph(this.kgGraphSchema, crossBorderRuleList);
      }
      Iterator<KgGraph<IVertexId>> pathIt = kgGraph.getPath(staticParameters, filter);
      long count = 0;
      while (pathIt.hasNext()) {
        KgGraph<IVertexId> path = pathIt.next();
        if (null == path) {
          continue;
        }
        Object[] row = impl.toRow(path);
        rows.add(row);
        count++;
        if (null != maxPathLimit && count >= maxPathLimit) {
          break;
        }
      }
    }
    log.info("LocalRDG select,,matchCount=" + rows.size());
    this.executionRecorder.stageResultWithDesc(
        "select(" + RunnerUtil.getReadableAsList(as) + ")", this.kgGraphList.size(), "select");
    return new LocalRow(cols, this, as, rows);
  }

  @Override
  public LocalRDG filter(Rule rule) {
    Tuple2<java.util.Set<String>, java.util.Set<Connection>> tuple2 =
        RunnerUtil.getRuleUseVertexAndEdgeSet(rule, this.kgGraphSchema);
    java.util.Set<String> vertexSet = new HashSet<>(tuple2._1());
    java.util.Set<String> edgeSet = new HashSet<>();
    for (Connection pc : tuple2._2()) {
      vertexSet.add(pc.source());
      vertexSet.add(pc.target());
      edgeSet.add(pc.alias());
    }

    java.util.List<String> exprStringSet = WareHouseUtils.getRuleList(rule);

    KgGraphSplitStaticParameters staticParameters =
        new KgGraphSplitStaticParameters(vertexSet, this.kgGraphSchema);
    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    long count = 0;
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      java.util.List<KgGraph<IVertexId>> resultList =
          RunnerUtil.filterKgGraph(
              kgGraph,
              vertexSet,
              edgeSet,
              this.kgGraphSchema,
              staticParameters,
              exprStringSet,
              this.maxPathLimit);
      count += resultList.size();
      newKgGraphList.addAll(resultList);
    }
    this.kgGraphList = newKgGraphList;
    log.info("Filter,rule=" + exprStringSet + ",matchCount=" + count);
    this.executionRecorder.stageResult("filter(" + exprStringSet + ")", this.kgGraphList.size());
    return this;
  }

  private java.util.List<String> convertGroupByVar2AliasSet(List<Var> by) {
    java.util.List<String> byAliasList = new ArrayList<>(by.size());
    JavaConversions.seqAsJavaList(by.toSeq()).forEach(var -> byAliasList.add(var.name()));
    return byAliasList;
  }

  private void groupByVariableThenAggregate(
      java.util.List<String> byAliasList,
      java.util.List<Tuple2<String, String>> byPropertyList,
      KgGraphListProcess kgGraphListProcess) {
    boolean isGlobalTopK = byAliasList.isEmpty();
    if (!byAliasList.isEmpty()) {
      if (!byAliasList.contains(this.kgGraphSchema.rootAlias())) {
        // KgGraph not key by expected alias
        String grouByAlias = byAliasList.remove(0);
        shuffleAndGroup(grouByAlias, false);
      } else {
        String nowRootAlias = this.kgGraphSchema.rootAlias();
        byAliasList.remove(nowRootAlias);
      }
    }

    String nowRootAlias = this.kgGraphSchema.rootAlias();

    if (byAliasList.isEmpty() && null == kgGraphListProcess) {
      this.kgGraphList = doMerge(this.kgGraphList, this.kgGraphSchema);
    } else {
      java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
      GroupByKgGraphImpl impl =
          new GroupByKgGraphImpl(
              byAliasList,
              byPropertyList,
              kgGraphListProcess,
              this.kgGraphSchema,
              this.maxPathLimit);
      java.util.List<KgGraph<IVertexId>> sameRootKgGraphList = new ArrayList<>();
      IVertexId lastKgGraphId = null;
      if (isGlobalTopK) {
        java.util.List<KgGraph<IVertexId>> kgGraphList = impl.groupReduce(this.kgGraphList);
        newKgGraphList.addAll(kgGraphList);
      } else {
        for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
          IVertexId nowVertexId = kgGraph.getVertex(nowRootAlias).get(0).getId();
          if (nowVertexId.equals(lastKgGraphId)) {
            sameRootKgGraphList.add(kgGraph);
          } else {
            if (null != lastKgGraphId) {
              java.util.List<KgGraph<IVertexId>> kgGraphList =
                  impl.groupReduce(sameRootKgGraphList);
              newKgGraphList.addAll(kgGraphList);
            }

            sameRootKgGraphList = new ArrayList<>();
            sameRootKgGraphList.add(kgGraph);
            lastKgGraphId = nowVertexId;
          }
        }
        if (null != lastKgGraphId) {
          java.util.List<KgGraph<IVertexId>> kgGraphList = impl.groupReduce(sameRootKgGraphList);
          newKgGraphList.addAll(kgGraphList);
        }
      }

      this.kgGraphList = newKgGraphList;
    }
  }

  @Override
  public LocalRDG orderBy(List<Var> groupKey, List<SortItem> sortItems, int limit) {
    KgGraphSortImpl impl =
        new KgGraphSortImpl(groupKey, sortItems, this.kgGraphSchema, limit, taskId);
    this.kgGraphList = impl.reduce(this.kgGraphList);
    this.executionRecorder.stageResult(
        "orderBy(" + RunnerUtil.getReadableByKey(groupKey) + ").limit(" + limit + ")",
        this.kgGraphList.size());
    return this;
  }

  @Override
  public LocalRDG groupBy(List<Var> by, Map<Var, Aggregator> aggregations) {
    java.util.List<String> tmpByAliasList = new ArrayList<>();
    java.util.List<Tuple2<String, String>> byPropertyList = new ArrayList<>();
    JavaConversions.seqAsJavaList(by.toSeq())
        .forEach(
            var -> {
              if (var instanceof NodeVar) {
                tmpByAliasList.add(var.name());
              } else if (var instanceof PropertyVar) {
                PropertyVar propertyVar = (PropertyVar) var;
                byPropertyList.add(new Tuple2<>(propertyVar.name(), propertyVar.field().name()));
              }
            });

    java.util.Set<String> validRootAlias = new HashSet<>();
    for (Connection connection : RunnerUtil.getConnectionSet(this.kgGraphSchema)) {
      if (tmpByAliasList.contains(connection.source())) {
        validRootAlias.add(connection.source());
      }
      if (!(connection instanceof PathConnection)) {
        if (tmpByAliasList.contains(connection.target())) {
          validRootAlias.add(connection.target());
        }
      }
    }
    if (CollectionUtils.isEmpty(validRootAlias)) {
      throw new UnsupportedOperationException("unsupported group by repeat edge", null);
    }

    java.util.List<String> byAliasList =
        RunnerUtil.sortGroupByAlias(tmpByAliasList, validRootAlias);
    if (CollectionUtils.isEmpty(byAliasList)) {
      throw new UnsupportedOperationException("unsupported group by property", null);
    }

    // agg first edge
    java.util.List<String> firstEdgeAliasList = RunnerUtil.getFirstEdgeAliasList(aggregations);
    if (null != firstEdgeAliasList && byAliasList.contains(this.kgGraphSchema.rootAlias())) {
      return aggregateFirstEdge(firstEdgeAliasList);
    }

    KgGraphListProcess kgGraphListProcess;
    java.util.Map<Var, Aggregator> aggregatorMap;
    if (null == aggregations || 0 == aggregations.size()) {
      // no aggregator, only merge
      kgGraphListProcess =
          new KgGraphListProcess() {
            @Override
            public java.util.List<KgGraph<IVertexId>> reduce(
                Collection<KgGraph<IVertexId>> kgGraphs) {
              KgGraphImpl kgGraph = new KgGraphImpl();
              assert kgGraphs instanceof java.util.List;
              kgGraph.merge((java.util.List<KgGraph<IVertexId>>) kgGraphs, kgGraphSchema);
              return Lists.newArrayList(kgGraph);
            }
          };
    } else {
      // do aggregate action
      aggregatorMap = JavaConversions.mapAsJavaMap(aggregations);
      KgGraphAggregateImpl impl =
          new KgGraphAggregateImpl(
              this.taskId,
              byAliasList.get(0),
              byAliasList,
              this.kgGraphSchema,
              aggregatorMap,
              this.maxPathLimit);
      impl.init();
      kgGraphListProcess =
          new KgGraphListProcess() {
            @Override
            public java.util.List<KgGraph<IVertexId>> reduce(
                Collection<KgGraph<IVertexId>> kgGraphs) {
              return Lists.newArrayList(impl.map(kgGraphs));
            }
          };
    }

    groupByVariableThenAggregate(byAliasList, byPropertyList, kgGraphListProcess);
    this.executionRecorder.stageResult(
        "groupBy(" + RunnerUtil.getReadableByKey(by) + ")", this.kgGraphList.size());
    return this;
  }

  private LocalRDG aggregateFirstEdge(java.util.List<String> firstEdgeAliasList) {
    KgGraphFirstEdgeAggImpl impl = new KgGraphFirstEdgeAggImpl(firstEdgeAliasList);
    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      newKgGraphList.add(impl.map(kgGraph));
    }
    this.kgGraphList = newKgGraphList;
    return this;
  }

  private java.util.List<KgGraph<IVertexId>> doMerge(
      java.util.List<KgGraph<IVertexId>> kgGraphList, PartialGraphPattern kgGraphSchema) {
    java.util.Map<IVertexId, KgGraph<IVertexId>> mergeMap = new java.util.TreeMap<>();
    for (KgGraph<IVertexId> kgGraph : kgGraphList) {
      IVertexId id = kgGraph.getVertex(kgGraphSchema.rootAlias()).get(0).getId();
      KgGraph<IVertexId> idKgGraph = mergeMap.computeIfAbsent(id, k -> new KgGraphImpl());
      idKgGraph.merge(Lists.newArrayList(kgGraph), kgGraphSchema);
    }
    return Lists.newArrayList(mergeMap.values());
  }

  private java.util.Map<IVertexId, java.util.List<KgGraph<IVertexId>>> groupByAlias(
      String vertexAlias) {
    KgGraphSplitStaticParameters staticParameters =
        new KgGraphSplitStaticParameters(Sets.newHashSet(vertexAlias), this.kgGraphSchema);
    java.util.Map<IVertexId, java.util.List<KgGraph<IVertexId>>> mergeMap =
        new java.util.TreeMap<>();
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      java.util.List<KgGraph<IVertexId>> splitList =
          kgGraph.split(
              Sets.newHashSet(vertexAlias),
              this.kgGraphSchema,
              staticParameters,
              null,
              this.maxPathLimit);
      for (KgGraph<IVertexId> kgGraphSpited : splitList) {
        IVertexId id = kgGraphSpited.getVertex(vertexAlias).get(0).getId();
        java.util.List<KgGraph<IVertexId>> sameIdKgGraph =
            mergeMap.computeIfAbsent(id, k -> new ArrayList<>());
        sameIdKgGraph.add(kgGraphSpited);
      }
    }
    return mergeMap;
  }

  private void shuffleAndGroup(String vertexAlias, boolean merge) {
    if (vertexAlias.equals(this.kgGraphSchema.rootAlias())) {
      // 当前KgGraph的根Alias和需要shuffler的根Alias相同，不需要做任何事情
      return;
    }

    this.kgGraphSchema = KgGraphSchema.schemaChangeRoot(this.kgGraphSchema, vertexAlias);
    java.util.Map<IVertexId, java.util.List<KgGraph<IVertexId>>> mergeMap =
        groupByAlias(vertexAlias);

    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    for (java.util.List<KgGraph<IVertexId>> kgGraphs : mergeMap.values()) {
      KgGraph<IVertexId> mergedKgGraph = new KgGraphImpl();
      if (merge) {
        mergedKgGraph.merge(kgGraphs, this.kgGraphSchema);
        newKgGraphList.add(mergedKgGraph);
      } else {
        newKgGraphList.addAll(kgGraphs);
      }
    }
    this.kgGraphList = newKgGraphList;
  }

  @Override
  public LocalRDG addFields(Map<Var, Expr> fields) {
    java.util.Map<Var, java.util.List<String>> addFieldsInfo = new HashMap<>();
    JavaConversions.mapAsJavaMap(fields)
        .forEach(
            new BiConsumer<Var, Expr>() {
              @Override
              public void accept(Var var, Expr expr) {
                addFieldsInfo.put(var, WareHouseUtils.getRuleList(expr));
              }
            });

    KgGraphAddFieldsImpl kgGraphAddFields =
        new KgGraphAddFieldsImpl(addFieldsInfo, this.kgGraphSchema, 0, this.taskId);
    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      newKgGraphList.addAll(kgGraphAddFields.map(kgGraph));
    }
    this.kgGraphList = newKgGraphList;
    this.executionRecorder.stageResult(
        "addFields(" + RunnerUtil.getReadableAddFields(addFieldsInfo) + ")",
        this.kgGraphList.size());
    return this;
  }

  @Override
  public LocalRDG dropFields(Set<Var> fields) {
    java.util.Set<Var> dropFieldSet = new HashSet<>(JavaConversions.asJavaCollection(fields));
    if (CollectionUtils.isEmpty(dropFieldSet)) {
      return this;
    }

    KgGraphDropFieldsImpl impl = new KgGraphDropFieldsImpl(dropFieldSet);
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      impl.doDropFields(kgGraph);
    }
    return this;
  }

  @Override
  public LocalRDG limit(long n) {
    if (this.kgGraphList.size() > n) {
      this.kgGraphList.subList(0, (int) n);
    }
    return this;
  }

  @Override
  public void show(int rows) {
    log.info("###############LocalRDGShowStart#############");
    try {
      if (null == this.kgGraphList) {
        log.info("null");
        return;
      }
      this.limit(rows);
      java.util.List<KgGraph<IVertexId>> result = this.kgGraphList;
      if (result.size() > rows) {
        result = Lists.newArrayList(result.subList(0, rows));
      }
      for (KgGraph<IVertexId> kgGraph : result) {
        kgGraph.show();
      }
    } finally {
      log.info("###############LocalRDGShowEnd###############");
    }
  }

  @Override
  public LocalRDG ddl(List<DDLOp> ddlOps) {
    KgGraphSplitStaticParameters staticParameters =
        new KgGraphSplitStaticParameters(null, this.kgGraphSchema);
    // convert to path
    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      Iterator<KgGraph<IVertexId>> it = kgGraph.getPath(staticParameters, null);
      while (it.hasNext()) {
        KgGraph<IVertexId> path = it.next();
        if (null == path) {
          continue;
        }
        newKgGraphList.add(path);
      }
    }
    this.kgGraphList = newKgGraphList;

    java.util.List<DDLOp> ddlOpList = Lists.newArrayList(JavaConversions.asJavaCollection(ddlOps));
    for (DDLOp ddlOp : ddlOpList) {
      if (ddlOp instanceof AddProperty) {
        AddProperty addProperty = (AddProperty) ddlOp;
        addProperty(
            addProperty.s().alias(),
            new Field(addProperty.propertyName(), addProperty.propertyType(), true));
      } else if (ddlOp instanceof AddPredicate) {
        AddPredicate addPredicate = (AddPredicate) ddlOp;
        addRelation(addPredicate);
      } else if (ddlOp instanceof AddVertex) {
        AddVertex addVertex = (AddVertex) ddlOp;
        addVertex(addVertex);
      }
    }
    this.kgGraphList = null;
    return this;
  }

  private void addProperty(String alias, Field property) {
    java.util.Map<String, GraphItemType> alias2TypeMap =
        new HashMap<>(JavaConversions.mapAsJavaMap(KgGraphSchema.alias2Type(this.kgGraphSchema)));
    GraphItemType type = alias2TypeMap.get(alias);
    long count = 0;
    if (!GraphItemType.VERTEX.equals(type)) {
      throw new NotImplementedException("only support add property on vertex", null);
    }

    // check now schema is root by vertex alias
    if (!alias.equals(this.kgGraphSchema.rootAlias())) {
      // need shuffle KgGraph
      shuffleAndGroup(alias, true);
    } else {
      this.kgGraphList = doMerge(this.kgGraphList, this.kgGraphSchema);
    }

    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      IVertex<IVertexId, IProperty> vertex = kgGraph.getVertex(alias).get(0);
      Object value = vertex.getValue().get(property.name());
      java.util.Map<String, Object> propertyMap = new HashMap<>();
      propertyMap.put(property.name(), value);
      count++;
      graphState.mergeVertexProperty(vertex.getId(), propertyMap, MergeTypeEnum.REPLACE, 0L);

      // add to result list
      this.resultVertexSet.add(
          new Vertex<>(vertex.getId(), new VertexProperty(new HashMap<>(propertyMap))));
    }

    this.executionRecorder.stageResultWithDesc(
        "addProperty(" + alias + "." + property.toString() + ")",
        count,
        "addProperty(" + alias + "." + property.toString() + ")");
    log.info(
        "LocalRDG.addProperty,sinkCount="
            + count
            + " addProperty="
            + alias
            + "."
            + property.toString());
  }

  private void addRelation(AddPredicate addPredicate) {
    ExtractRelationImpl impl =
        new ExtractRelationImpl(addPredicate, this.kgGraphSchema, 0, this.taskId);
    java.util.Set<IEdge<IVertexId, IProperty>> allEdgeSet = new HashSet<>();
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      IEdge<IVertexId, IProperty> edge = impl.extractEdge(kgGraph);
      allEdgeSet.add(edge);

      // add to result list
      this.resultEdgeSet.add(edge);

      if (impl.withReverseEdge()) {
        // 反向边
        allEdgeSet.add(impl.createReverseEdge(edge));
      }
    }

    SinkRelationImpl sinkImpl = new SinkRelationImpl(this.graphState, 0);
    long sinkCount = sinkImpl.sink(allEdgeSet);
    this.executionRecorder.stageResultWithDesc(
        "addRelation(" + RunnerUtil.getReadableAddPredicate(addPredicate) + ")",
        sinkCount,
        "addRelation(" + addPredicate.predicate().label() + ")");
    log.info(
        "LocalRDG.addRelation,sinkCount=" + sinkCount + " addPredicate=" + addPredicate.toString());
  }

  private void addVertex(AddVertex addVertex) {
    ExtractVertexImpl impl = new ExtractVertexImpl(addVertex, this.kgGraphSchema, 0, this.taskId);

    long count = 0;
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      IVertex<IVertexId, IProperty> willAddedVertex = impl.extractVertex(kgGraph);

      // add to result list
      this.resultVertexSet.add(willAddedVertex);
      count++;
    }

    this.executionRecorder.stageResultWithDesc(
        "addVertex(" + RunnerUtil.getReadableAddVertex(addVertex) + ")", count, "addVertex");
    log.info("LocalRDG.addVertex,sinkCount=" + count);
  }

  @Override
  public LocalRDG cache() {
    // local runner do nothing
    return this;
  }

  private static java.util.Map<GroupByKeyItem, java.util.List<KgGraph<IVertexId>>> groupByAliasList(
      List<String> vertexAliasList,
      java.util.List<KgGraph<IVertexId>> kgGraphList,
      PartialGraphPattern kgGraphSchema,
      Long maxPathLimit) {
    java.util.Set<String> splitAliasSet =
        new HashSet<>(JavaConversions.seqAsJavaList(vertexAliasList));
    KgGraphSplitStaticParameters staticParameters =
        new KgGraphSplitStaticParameters(splitAliasSet, kgGraphSchema);
    scala.collection.immutable.List<Var> varList =
        Convert2ScalaUtil.toScalaList(
            JavaConversions.seqAsJavaList(vertexAliasList).stream()
                .map((Function<String, Var>) s -> new NodeVar(s, null))
                .collect(Collectors.toList()));
    java.util.Map<GroupByKeyItem, java.util.List<KgGraph<IVertexId>>> mergeMap =
        new java.util.HashMap<>();
    for (KgGraph<IVertexId> kgGraph : kgGraphList) {
      java.util.List<KgGraph<IVertexId>> splitList =
          kgGraph.split(splitAliasSet, kgGraphSchema, staticParameters, null, maxPathLimit);
      for (KgGraph<IVertexId> kgGraphSpited : splitList) {
        Object[] keys = RunnerUtil.getVarFromKgGraph(kgGraph, varList);
        java.util.List<KgGraph<IVertexId>> sameIdKgGraph =
            mergeMap.computeIfAbsent(new GroupByKeyItem(keys), k -> new ArrayList<>());
        sameIdKgGraph.add(kgGraphSpited);
      }
    }
    return mergeMap;
  }

  private static final java.util.Set<JoinType> SUPPORTED_JOIN_TYPE = new HashSet<>();

  static {
    SUPPORTED_JOIN_TYPE.add(LeftOuterJoin$.MODULE$);
    SUPPORTED_JOIN_TYPE.add(InnerJoin$.MODULE$);
    SUPPORTED_JOIN_TYPE.add(FullOuterJoin$.MODULE$);
  }

  @Override
  public LocalRDG join(
      LocalRDG other,
      JoinType joinType,
      List<Tuple2<String, String>> onAlias,
      Map<Var, Var> lhsSchemaMapping,
      Map<Var, Var> rhsSchemaMapping) {

    try {
      if (null == other.getKgGraphList()) {
        // other KgGraph is ddl result, nothing to join
        return this;
      }

      if (!SUPPORTED_JOIN_TYPE.contains(joinType)) {
        throw new NotImplementedException("not supported join type " + joinType, null);
      }

      java.util.List<String> joinAlias =
          RunnerUtil.joinAliasAfterMapping(onAlias, lhsSchemaMapping);
      String splitVertexAlias = joinAlias.get(0);

      // mapping left
      java.util.List<KgGraph<IVertexId>> left = this.kgGraphList;
      PartialGraphPattern leftSchema = this.kgGraphSchema;
      KgGraphRenameImpl leftRenameImpl = new KgGraphRenameImpl(lhsSchemaMapping);
      if (leftRenameImpl.needAction()) {
        left = left.stream().map(leftRenameImpl::rename).collect(Collectors.toList());
        leftSchema = KgGraphSchema.schemaAliasMapping(leftSchema, lhsSchemaMapping);
      }

      // split left
      boolean leftNeedSplit =
          joinAlias.size() > 1 || !splitVertexAlias.equals(leftSchema.rootAlias());
      if (leftNeedSplit) {
        leftSchema = KgGraphSchema.schemaChangeRoot(leftSchema, splitVertexAlias);
        java.util.Map<GroupByKeyItem, java.util.List<KgGraph<IVertexId>>> leftMergeMap =
            groupByAliasList(
                Convert2ScalaUtil.toScalaList(joinAlias), left, leftSchema, this.maxPathLimit);
        left = new ArrayList<>();
        for (java.util.List<KgGraph<IVertexId>> kgGraphs : leftMergeMap.values()) {
          left.addAll(kgGraphs);
        }
      }

      // mapping right
      java.util.List<KgGraph<IVertexId>> right = other.kgGraphList;
      PartialGraphPattern rightSchema = other.kgGraphSchema;
      rhsSchemaMapping = RunnerUtil.newRhsSchemaMapping(rhsSchemaMapping, onAlias);
      KgGraphRenameImpl rightRenameImpl = new KgGraphRenameImpl(rhsSchemaMapping);
      if (rightRenameImpl.needAction()) {
        right = right.stream().map(rightRenameImpl::rename).collect(Collectors.toList());
        rightSchema = KgGraphSchema.schemaAliasMapping(rightSchema, rhsSchemaMapping);
      }

      // split right
      boolean rightNeedSplit =
          joinAlias.size() > 1 || !splitVertexAlias.equals(rightSchema.rootAlias());
      if (rightNeedSplit) {
        rightSchema = KgGraphSchema.schemaChangeRoot(rightSchema, splitVertexAlias);
        java.util.Map<GroupByKeyItem, java.util.List<KgGraph<IVertexId>>> rightMergeMap =
            groupByAliasList(
                Convert2ScalaUtil.toScalaList(joinAlias), right, rightSchema, this.maxPathLimit);
        right = new ArrayList<>();
        for (java.util.List<KgGraph<IVertexId>> kgGraphs : rightMergeMap.values()) {
          right.addAll(kgGraphs);
        }
      }

      // change direction
      java.util.Map<String, Tuple2<Direction, Direction>> diffEdgeDirectionMap =
          RunnerUtil.getOverlapEdgeDirectionDiff(leftSchema, rightSchema);
      if (!diffEdgeDirectionMap.isEmpty()) {
        KgGraphEdgeDirectionImpl edgeDirection = new KgGraphEdgeDirectionImpl(diffEdgeDirectionMap);
        left =
            left.stream()
                .map(
                    new Function<KgGraph<IVertexId>, KgGraph<IVertexId>>() {
                      @Override
                      public KgGraph<IVertexId> apply(KgGraph<IVertexId> kgGraph) {
                        return edgeDirection.convert((KgGraphImpl) kgGraph);
                      }
                    })
                .collect(Collectors.toList());
      }

      // convert to join item
      java.util.List<JoinItem> joinItemList =
          left.stream().map(kgGraph -> new JoinItem(true, kgGraph)).collect(Collectors.toList());
      joinItemList.addAll(
          right.stream().map(kgGraph -> new JoinItem(false, kgGraph)).collect(Collectors.toList()));

      // do join
      ReasonerJoinImpl reasonerJoin =
          new ReasonerJoinImpl(joinType, joinAlias, leftSchema, rightSchema, this.maxPathLimit);
      this.kgGraphList = reasonerJoin.join(joinItemList);

      this.kgGraphSchema = KgGraphSchema.getAfterJoinSchema(leftSchema, rightSchema);
    } finally {
      other.clear();
      this.executionRecorder.leaveRDG();
      this.executionRecorder.stageResult(
          "join(" + RunnerUtil.getReadableJoinString(onAlias, rhsSchemaMapping) + ")",
          this.kgGraphList.size());
    }
    return this;
  }

  @Override
  public LocalRDG unfold(List<Tuple2<RichVar, List<Var>>> mapping) {
    UnfoldRepeatEdgeInfo unfoldRepeatEdgeInfo =
        RunnerUtil.getUnfoldEdgeInfo(mapping, this.kgGraphSchema);
    UnfoldEdgeImpl impl = new UnfoldEdgeImpl(unfoldRepeatEdgeInfo);
    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      java.util.List<KgGraph<IVertexId>> rst = impl.unfold((KgGraphImpl) kgGraph);
      newKgGraphList.addAll(rst);
    }
    this.kgGraphList = newKgGraphList;
    this.kgGraphSchema = KgGraphSchema.schemaChangeRoot(this.kgGraphSchema, null);

    // duplicates need to be removed after unfold
    this.groupByVariableThenAggregate(
        Lists.newArrayList(
            unfoldRepeatEdgeInfo.getAnchorVertexAlias(), unfoldRepeatEdgeInfo.getFoldVertexAlias()),
        new ArrayList<>(),
        new UnfoldReduceDuplicateImpl(this.kgGraphSchema, unfoldRepeatEdgeInfo.getEdgeAlias()));
    return this;
  }

  @Override
  public LocalRDG union(LocalRDG other) {
    throw new NotImplementedException("union not supported yet", null);
  }

  @Override
  public LocalRDG fold(List<Tuple2<List<Var>, RichVar>> foldMapping) {
    FoldRepeatEdgeInfo foldRepeatEdgeInfo =
        RunnerUtil.getFoldRepeatEdgeInfo(foldMapping, this.kgGraphSchema);
    this.kgGraphSchema = KgGraphSchema.foldPathEdgeSchema(this.kgGraphSchema, foldRepeatEdgeInfo);

    FoldEdgeImpl impl = new FoldEdgeImpl(kgGraphSchema, foldRepeatEdgeInfo);
    java.util.List<KgGraph<IVertexId>> newKgGraphList = new ArrayList<>();
    for (KgGraph<IVertexId> kgGraph : this.kgGraphList) {
      java.util.List<KgGraph<IVertexId>> rst = impl.fold((KgGraphImpl) kgGraph);
      newKgGraphList.addAll(rst);
    }
    this.kgGraphList = newKgGraphList;
    return this;
  }

  private void clear() {
    this.kgGraphList = null;
    this.kgGraphSchema = null;
    this.resultVertexSet.clear();
    this.resultEdgeSet.clear();
  }

  // use for sub rdg
  public java.util.List<KgGraph<IVertexId>> getKgGraphList() {
    return this.kgGraphList;
  }

  public PartialGraphPattern getKgGraphSchema() {
    return this.kgGraphSchema;
  }

  /** get ddl result */
  public LocalReasonerResult getResult() {
    return new LocalReasonerResult(
        Lists.newArrayList(resultVertexSet), Lists.newArrayList(resultEdgeSet), true);
  }

  /**
   * get all RDG Edges and Nodes
   *
   * @return
   */
  public LocalReasonerResult getRDGGraph() {
    LocalReasonerResult localReasonerResult = this.getResult();
    this.kgGraphList.forEach(
        graph -> {
          for (String alias : graph.getVertexAlias()) {
            localReasonerResult.getVertexList().addAll(graph.getVertex(alias));
          }
          for (String alias : graph.getEdgeAlias()) {
            localReasonerResult.getEdgeList().addAll(graph.getEdge(alias));
          }
        });
    return localReasonerResult;
  }

  /**
   * Setter method for property <tt>maxPathLimit</tt>.
   *
   * @param maxPathLimit value to be assigned to property maxPathLimit
   */
  public void setMaxPathLimit(Long maxPathLimit) {
    this.maxPathLimit = maxPathLimit;
  }

  /**
   * Setter method for property <tt>strictMaxPathLimit</tt>.
   *
   * @param strictMaxPathLimit value to be assigned to property strictMaxPathLimit
   */
  public void setStrictMaxPathLimit(Long strictMaxPathLimit) {
    this.strictMaxPathLimit = strictMaxPathLimit;
  }
}
