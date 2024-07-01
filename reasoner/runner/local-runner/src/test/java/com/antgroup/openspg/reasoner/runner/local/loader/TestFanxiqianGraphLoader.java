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

public class TestFanxiqianGraphLoader extends AbstractLocalGraphLoader {
  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return Lists.newArrayList(
        constructionVertex("张三", "Test.User", "id", "张三", "name", "高级健康保险", "nightTrader", "3"),
        constructionVertex(
            "白领-医生", "Test.UserFeature", "id", "白领-医生", "name", "医生", "nightTrader", "3"),
        constructionVertex(
            "学生-就读中学", "Test.UserFeature", "id", "学生-就读中学", "name", "就读中学", "nightTrader", "3"),
        constructionVertex(
            "职业", "Test.TaxOfUserFeature", "id", "职业", "name", "职业", "nightTrader", "3"),
        constructionVertex(
            "学习阶段", "Test.TaxOfUserFeature", "id", "学习阶段", "name", "学习阶段", "nightTrader", "3"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionEdge("白领-医生", "newedge", "职业"), constructionEdge("学生-就读中学", "newedge", "学习阶段"));
  }
}
