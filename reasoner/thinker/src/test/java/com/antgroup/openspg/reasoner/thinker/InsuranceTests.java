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

package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.catalog.ResourceLogicCatalog;
import com.antgroup.openspg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Node;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Predicate;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

public class InsuranceTests {

  private GraphState<IVertexId> buildGraphState() {
    Vertex<IVertexId, IProperty> v1 = GraphUtil.makeVertex("肺钙化", "InsDisease");
    Vertex<IVertexId, IProperty> v2 = GraphUtil.makeVertex("肺部肿物或结节", "InsDisease");
    Vertex<IVertexId, IProperty> v3 = GraphUtil.makeVertex("肺炎性假瘤", "InsDisease");
    Vertex<IVertexId, IProperty> v4 = GraphUtil.makeVertex("肺病", "InsDisease");
    Vertex<IVertexId, IProperty> v5 = GraphUtil.makeVertex("肺癌", "InsDisease");
    Vertex<IVertexId, IProperty> v6 = GraphUtil.makeVertex("既往症", "InsDiseaseDisclaim");
    Vertex<IVertexId, IProperty> v7 = GraphUtil.makeVertex("好医保0免赔", "InsClause");
    Vertex<IVertexId, IProperty> v8 = GraphUtil.makeVertex("好医保0免赔", "InsComProd");


    Edge e1 = GraphUtil.makeEdge(v1, v2, "child");
    Edge e2 = GraphUtil.makeEdge(v2, v3, "child");
    Edge e3 = GraphUtil.makeEdge(v4, v3, "child");
    Edge e4 = GraphUtil.makeEdge(v4, v5, "child");
    Edge e5 = GraphUtil.makeEdge(v1, v5, "child");

    Edge e6 = GraphUtil.makeEdge(v2, v2, "evolve");
    Edge e7 = GraphUtil.makeEdge(v5, v5, "evolve");
    Edge e8 = GraphUtil.makeEdge(v3, v2, "evolve");

    Edge e9 = GraphUtil.makeEdge(v2, v6, "disclaimClause", "disclaimType", "既往");
    Edge e10 = GraphUtil.makeEdge(v6, v7, "clauseVersion");
    Edge e11 = GraphUtil.makeEdge(v7, v8, "insClauseVersion");


    List<IVertex<IVertexId, IProperty>> vertexList = Arrays.asList(v1, v2, v3, v4, v5);
    List<IEdge<IVertexId, IProperty>> edgeList = Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11);
    return GraphUtil.buildMemState(vertexList, edgeList);
  }

  @Test
  public void directEvolve() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/InsuranceRules.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);

    List<Result> triples =
        thinker.find(
            new Entity("肺钙化", "InsDisease"),
            new Predicate("directEvolve"),
            new Entity("肺病", "InsDisease"),
            null);
    Assert.assertTrue(triples.size() == 0);
  }

  @Test
  public void inDirectEvolveForward() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/InsuranceRules.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);

    List<Result> triples =
        thinker.find(
            new Entity("肺钙化", "InsDisease"),
            new Predicate("inDirectEvolve"),
            new Node("InsDisease"),
            new HashMap<>());
    Assert.assertTrue(triples.size() == 3);
  }

  @Test
  public void childDisease() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/InsuranceRules.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);

    List<Result> triples =
        thinker.find(
            new Entity("肺病", "InsDisease"),
            new Predicate("inDirectEvolve"),
            new Node("InsDisease"),
            new HashMap<>());
    Assert.assertTrue(triples.size() == 2);
  }

  @Test
  public void disclaim() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/InsuranceRules.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);

    List<Result> triples =
            thinker.find(
                    new Entity("肺部肿物或结节", "InsDisease"),
                    new Predicate("disclaim"),
                    new Entity("好医保0免赔", "InsComProd"),
                    new HashMap<>());
    Assert.assertTrue(triples.size() == 1);
  }
}
