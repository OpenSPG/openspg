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

package com.antgroup.openspg.reasoner.local.loader;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.local.load.graph.AbstractLocalGraphLoader;
import com.google.common.collect.Lists;
import java.util.List;

public class TestMultiLabelGraphLoader extends AbstractLocalGraphLoader {
  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return Lists.newArrayList(
        constructionVertex("S", "CustFundKG.Account", "accountId", "20881"),
        constructionVertex("D", "CustFundKG.Account", "accountId", "20882"),
        constructionVertex("B_pay", "CustFundKG.Alipay", "accountId", "20881"),
        constructionVertex("B_card", "CustFundKG.BankCard", "accountId", "20881"),
        constructionVertex("C_pay", "CustFundKG.Alipay", "accountId", "20882"),
        constructionVertex("C_card", "CustFundKG.BankCard", "accountId", "20882"),
        constructionVertex("20882", "STD.AlipayAccount"),
        constructionVertex("20881", "STD.AlipayAccount"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionEdge("S", "accountId", "20881"),
        constructionEdge("D", "accountId", "20882"),
        constructionEdge("B_pay", "accountId", "20881"),
        constructionEdge("B_card", "accountId", "20881"),
        constructionEdge("C_pay", "accountId", "20882"),
        constructionEdge("C_card", "accountId", "20882"),
        constructionEdge("B_pay", "transfer", "C_pay", "amount", 100),
        constructionEdge("B_pay", "transfer", "C_card", "amount", 100),
        constructionEdge("B_card", "transfer", "C_pay", "amount", 100),
        constructionEdge("B_card", "transfer", "C_card", "amount", 100),
        constructionEdge("B_pay", "consume", "C_pay", "amount", 100),
        constructionEdge("B_pay", "consume", "C_card", "amount", 100),
        constructionEdge("B_card", "consume", "C_pay", "amount", 100),
        constructionEdge("B_card", "consume", "C_card", "amount", 100));
  }
}
