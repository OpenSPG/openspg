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

package com.antgroup.openspg.reasoner.runner.local;

import com.antgroup.openspg.reasoner.catalog.CatalogFactory;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface;
import com.antgroup.openspg.reasoner.lube.physical.operators.PhysicalOperator;
import com.antgroup.openspg.reasoner.lube.physical.operators.Select;
import com.antgroup.openspg.reasoner.lube.physical.operators.Start;
import com.antgroup.openspg.reasoner.lube.physical.util.PhysicalOperatorUtil;
import com.antgroup.openspg.reasoner.parser.OpenspgDslParser;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.runner.local.impl.LocalPropertyGraph;
import com.antgroup.openspg.reasoner.runner.local.impl.LocalReasonerSession;
import com.antgroup.openspg.reasoner.runner.local.impl.LocalRunnerThreadPool;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.runner.local.rdg.LocalRDG;
import com.antgroup.openspg.reasoner.runner.local.rdg.LocalRow;
import com.antgroup.openspg.reasoner.runner.local.rdg.TypeTags;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.collection.JavaConversions;

@Slf4j
public class LocalReasonerRunner {

  /** run dsl task on local runner */
  public LocalReasonerResult run(LocalReasonerTask task) {
    try {
      return doRun(task);
    } catch (Throwable e) {
      log.error("KGReasonerLocalRunner,error", e);
      return new LocalReasonerResult("KGReasonerLocalRunner,error:" + e.getMessage());
    }
  }

  private LocalReasonerResult doRun(LocalReasonerTask task) {
    // load graph
    GraphState<IVertexId> graphState = loadGraph(task);

    // plan
    LocalReasonerSession session = task.getSession();
    List<PhysicalOperator<LocalRDG>> dslDagList = task.getDslDagList();
    if (CollectionUtils.isEmpty(dslDagList)) {
      if (StringUtils.isEmpty(task.getDsl())) {
        throw new RuntimeException("please provide dsl");
      }

      // create catalog
      Catalog catalog = task.getCatalog();
      if (null == catalog) {
        catalog = CatalogFactory.createCatalog(task.getParams(), task.getConnInfo());
        catalog.init();
      }

      if (!task.getParams().containsKey(Constants.START_ALIAS)
          && !task.getParams().containsKey(Constants.START_LABEL)
          && task.getStartIdList() != null
          && task.getStartIdList().size() > 0) {
        task.getParams().put(Constants.START_LABEL, task.getStartIdList().get(0)._2);
      }
      ParserInterface parser = new OpenspgDslParser();
      session = new LocalReasonerSession(parser, catalog, TypeTags.rdgTypeTag(), graphState);
      dslDagList =
          Lists.newArrayList(
              JavaConversions.asJavaCollection(
                  session.plan(
                      task.getDsl(), Convert2ScalaUtil.toScalaImmutableMap(task.getParams()))));
    }

    LocalReasonerResult result = null;
    for (int i = 0; i < dslDagList.size(); ++i) {
      Map<String, String> idFilterMaps =
          JavaConversions.mapAsJavaMap(session.getIdFilterParameters());
      Map<String, Object> taskRunningContext =
          RunnerUtil.getTaskRunningContext(session, task.getParams());

      RuleRunner.getInstance().putRuleRunningContext(task.getId(), taskRunningContext);
      boolean isLastDsl = (i + 1 == dslDagList.size());

      if (isLastDsl) {
        Start<LocalRDG> start =
            PhysicalOperatorUtil.getStartOp(
                dslDagList.get(i),
                com.antgroup.openspg.reasoner.runner.local.rdg.TypeTags.rdgTypeTag());

        if (idFilterMaps != null && idFilterMaps.size() != 0) {
          String startAliasName = start.alias();
          if (idFilterMaps.containsKey(startAliasName)) {
            String parameter = idFilterMaps.get(startAliasName);
            Object obj = taskRunningContext.get(parameter);
            List<String> originIds = new ArrayList<>();
            if (obj instanceof Object[]) {
              Object[] ids = (Object[]) obj;
              for (Object id : ids) {
                originIds.add(id.toString());
              }
            } else {
              originIds.add(obj.toString());
            }
            List<Tuple2<String, String>> startIdList = new ArrayList<>();

            for (String type : JavaConversions.setAsJavaSet(start.types())) {
              for (Object id : originIds) {
                startIdList.add(new Tuple2<>(id.toString(), type));
              }
            }
            task.setStartIdList(startIdList);
          }
        }
      }
      PhysicalOperator<LocalRDG> physicalOpRoot = dslDagList.get(i);
      LocalPropertyGraph localPropertyGraph =
          (LocalPropertyGraph)
              physicalOpRoot.context().graphSession().getGraph(Catalog.defaultGraphName());
      localPropertyGraph.setTask(task);
      localPropertyGraph.setThreadPoolExecutor(LocalRunnerThreadPool.getThreadPoolExecutor(task));
      localPropertyGraph.setExecutorTimeoutMs(task.getExecutorTimeoutMs());
      if (CollectionUtils.isNotEmpty(task.getStartIdList()) && isLastDsl) {
        localPropertyGraph.setStartIdTuple2List(task.getStartIdList());
      } else {
        localPropertyGraph.setStartIdTuple2List(null);
      }

      if (physicalOpRoot instanceof Select) {
        String isGraphOutput =
            String.valueOf(
                task.getParams().computeIfAbsent(ConfigKey.KG_REASONER_OUTPUT_GRAPH, k -> "false"));
        if ("true".equals(isGraphOutput)) {
          LocalRDG rdg = ((Select<LocalRDG>) physicalOpRoot).in().rdg();
          result = rdg.getRDGGraph();
        } else {
          LocalRow row = (LocalRow) ((Select<LocalRDG>) physicalOpRoot).row();
          result = row.getResult();
        }
      } else {
        LocalRDG rdg = physicalOpRoot.rdg();
        result = rdg.getResult();
      }
      log.info("dsl,index=" + i + ",result=" + result.toString());
    }

    return result;
  }

  protected GraphState<IVertexId> loadGraph(LocalReasonerTask task) {
    GraphState<IVertexId> graphState = task.getGraphState();
    if (null != graphState) {
      return graphState;
    }

    String graphStateClass = task.getGraphStateClassName();
    if (StringUtils.isNotEmpty(graphStateClass)) {
      try {
        graphState =
            (GraphState<IVertexId>)
                Class.forName(graphStateClass)
                    .getConstructor(String.class)
                    .newInstance(task.getGraphStateInitString());
      } catch (Exception e) {
        throw new RuntimeException("can not create graph state from " + graphStateClass, e);
      }
      return graphState;
    }

    String graphLoadClass = task.getGraphLoadClass();
    MemGraphState memGraphState = new MemGraphState();
    AbstractLocalGraphLoader graphLoader;
    try {
      graphLoader =
          (AbstractLocalGraphLoader) Class.forName(graphLoadClass).getConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("can not create graph loader from name " + graphLoadClass, e);
    }
    graphLoader.setGraphState(memGraphState);
    graphLoader.load();
    task.setGraphState(memGraphState);
    return memGraphState;
  }
}
