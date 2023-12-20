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

package com.antgroup.openspg.reasoner.runner.local.main;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.generator.AbstractGraphGenerator;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
import com.antgroup.openspg.reasoner.recorder.DefaultRecorder;
import com.antgroup.openspg.reasoner.runner.local.KGReasonerLocalRunner;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import scala.Tuple2;

public class LocalRunnerTestFactory {

  public static void runTest(
      String dsl,
      AbstractLocalGraphLoader graphLoader,
      AssertFunction assertFunc,
      Map<String, Object> params) {
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    MemGraphState memGraphState = new MemGraphState();
    graphLoader.setGraphState(memGraphState);
    graphLoader.load();
    task.setGraphState(memGraphState);

    // catalog
    task.setCatalog(getCatalogFromGraphData(graphLoader));

    // enable subquery
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);
    task.setExecutorTimeoutMs(30 * 60 * 1000);

    task.setExecutionRecorder(new DefaultRecorder());

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(task.getExecutionRecorder().toReadableString());
    assertFunc.assertResult(result);
  }

  private static Catalog getCatalogFromGraphData(AbstractGraphGenerator graph) {
    Tuple2<List<IVertex<IVertexId, IProperty>>, List<IEdge<IVertexId, IProperty>>> graphData =
        graph.getGraphData();
    Map<String, java.util.Set<String>> schemaMap = new HashMap<>();
    for (IVertex<IVertexId, IProperty> vertex : graphData._1()) {
      java.util.Set<String> propertySet =
          schemaMap.computeIfAbsent(vertex.getId().getType(), k -> new HashSet<>());
      propertySet.addAll(vertex.getValue().getKeySet());
    }
    for (IEdge<IVertexId, IProperty> edge : graphData._2()) {
      java.util.Set<String> propertySet =
          schemaMap.computeIfAbsent(edge.getType(), k -> new HashSet<>());
      propertySet.addAll(edge.getValue().getKeySet());
    }
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    for (Map.Entry<String, java.util.Set<String>> entry : schemaMap.entrySet()) {
      schema.put(entry.getKey(), Convert2ScalaUtil.toScalaImmutableSet(entry.getValue()));
    }
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    return catalog;
  }

  public interface AssertFunction {
    void assertResult(LocalReasonerResult result);
  }
}
