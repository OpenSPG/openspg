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

public class TestInsProductGraphLoader extends AbstractLocalGraphLoader {
  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return Lists.newArrayList(
        constructionVertex("保险产品", "InsProduct.Product", "name", "保险产品"),
        constructionVertex("轻症疾病保险金", "InsProduct.Liability", "name", "轻症疾病保险金"),
        constructionVertex("中症疾病保险金", "InsProduct.Liability", "name", "中症疾病保险金"),
        constructionVertex("重症疾病保险金", "InsProduct.Liability", "name", "重症疾病保险金"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionEdge("保险产品", "includeLiability", "轻症疾病保险金"),
        constructionEdge("保险产品", "includeLiability", "中症疾病保险金"),
        constructionEdge("保险产品", "includeLiability", "重症疾病保险金"));
  }
}
