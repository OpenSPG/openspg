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

import java.util.List;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.google.common.collect.Lists;

public class TestRoadGraphLoader extends AbstractLocalGraphLoader {
  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return Lists.newArrayList(
        constructionVertex("张三", "Road.Researcher"),
        constructionVertex("江西yy校", "Road.Area", "name", "江西yy校"),
            constructionVertex("湖北xx地", "Road.Area", "name", "湖北xx地"),
            constructionVertex("江西省", "Road.AdministrativeRegion", "name", "江西省"),
            constructionVertex("湖北省", "Road.AdministrativeRegion", "name", "湖北省"),

            constructionVertex("E1", "Road.Event", "kgstartDateRaw", "20230901"),
            constructionVertex("E2", "Road.Event", "kgstartDateRaw", "20230901"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionEdge("E1", "subject", "张三"),
        constructionEdge("E1", "object", "江西yy校"),
            constructionEdge("E1", "province", "江西省"),

            constructionEdge("E2", "subject", "张三"),
            constructionEdge("E2", "object", "湖北xx地"),
            constructionEdge("E2", "province", "湖北省")
    );
  }
}
