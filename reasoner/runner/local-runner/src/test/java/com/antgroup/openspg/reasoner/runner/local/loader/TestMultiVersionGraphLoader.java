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

public class TestMultiVersionGraphLoader extends AbstractLocalGraphLoader {
  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return Lists.newArrayList(
        constructionVertex("1", "CustFundKG.Account"),
        constructionVertex("2", "CustFundKG.Account"),
        constructionVertex("3", "CustFundKG.Account"),
        constructionVertex("4", "CustFundKG.Account"),
        constructionVertex("5", "CustFundKG.Account"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionVersionEdge(
            "2", "accountFundContact", "1", 123, "sumAmt", 1000, "transDate", "20230630"),
        constructionVersionEdge(
            "3", "accountFundContact", "1", 123, "sumAmt", 1000, "transDate", "20230601"),
        constructionVersionEdge(
            "4", "accountFundContact", "1", 123, "sumAmt", 1000, "transDate", "20230602"),
        constructionVersionEdge(
            "5", "accountFundContact", "1", 123, "sumAmt", 1000, "transDate", "20230514"));
  }
}
