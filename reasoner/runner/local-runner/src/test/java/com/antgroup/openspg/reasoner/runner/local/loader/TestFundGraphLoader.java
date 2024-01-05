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

package com.antgroup.openspg.reasoner.runner.local.loader;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.google.common.collect.Lists;
import java.util.List;

public class TestFundGraphLoader extends AbstractLocalGraphLoader {
  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return Lists.newArrayList(
        constructionVertex("u1", "AttributePOC.User"),
        constructionVertex("m1", "AttributePOC.PortfolioManager"),
        constructionVertex("f1", "AttributePOC.Fund"),
        constructionVertex("f2", "AttributePOC.Fund"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionEdge("u1", "holdProduct", "f1", "holdRet", 0.02),
        constructionEdge("m1", "underControl", "f1"),
        constructionEdge("u1", "followPM", "m1", "holdRet", 0.01, "times", 4),
        constructionEdge("u1", "holdProduct", "f2", "holdRet", 0.02),
        constructionEdge("m1", "underControl", "f2"));
  }
}
