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
        constructionVertex("张三", "XIQIAN.User", "id", "张三", "name", "高级健康保险", "nightTrader", "3"),
        constructionVertex(
            "交易风险-凌晨交易多",
            "XIQIAN.UserBehavior",
            "id",
            "交易风险-凌晨交易多",
            "name",
            "频繁交易",
            "nightTrader",
            "3"),
        constructionVertex(
            "资金来源-资金交易多",
            "XIQIAN.UserBehavior",
            "id",
            "资金来源-资金交易多",
            "name",
            "频繁交易",
            "nightTrader",
            "3"),
        constructionVertex(
            "资金来源", "XIQIAN.TaxOfUserBehavior", "id", "资金来源", "name", "资金来源", "nightTrader", "3"),
        constructionVertex(
            "交易行为", "XIQIAN.TaxOfUserBehavior", "id", "交易行为", "name", "频繁交易", "nightTrader", "3"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionEdge("交易风险-凌晨交易多", "newedge", "交易行为"),
        constructionEdge("资金来源-资金交易多", "newedge", "资金来源"));
  }
}
