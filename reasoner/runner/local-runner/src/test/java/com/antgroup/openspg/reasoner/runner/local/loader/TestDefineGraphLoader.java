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

package com.antgroup.openspg.reasoner.runner.local.loader;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.google.common.collect.Lists;
import java.util.List;

public class TestDefineGraphLoader extends AbstractLocalGraphLoader {
  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return Lists.newArrayList(
        constructionVertex("user1", "ABM.User"),
        constructionVertex("192.168.1.1", "ABM.IP", "country", "柬埔寨"),
        constructionVertex("black.net", "DomainFamily"),
        constructionVertex("one.black.net", "Domain"),
        constructionVertex("two.black.net", "Domain"),
        constructionVertex("black_app_1", "Pkg", "is_black", true),
        constructionVertex("black_app_2", "Pkg", "is_black", true),
        constructionVertex("u1", "User"),
        constructionVertex("u2", "User"),
        constructionVertex("u3", "User"),
        constructionVertex("u4", "User"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionEdge("user1", "acc2ip", "192.168.1.1", "ipUse180dCnt", 5),
        constructionEdge("one.black.net", "belong", "black.net"),
        constructionEdge("two.black.net", "belong", "black.net"),
        constructionEdge("black_app_1", "use", "one.black.net"),
        constructionEdge("black_app_2", "use", "two.black.net"),
        constructionEdge("u1", "visit", "one.black.net"),
        constructionEdge("u2", "visit", "one.black.net"),
        constructionEdge("u3", "visit", "two.black.net"),
        constructionEdge("u4", "visit", "two.black.net"));
  }
}
