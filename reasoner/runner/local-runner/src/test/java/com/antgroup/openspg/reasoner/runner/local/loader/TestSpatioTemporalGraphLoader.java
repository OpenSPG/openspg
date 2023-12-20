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

public class TestSpatioTemporalGraphLoader extends AbstractLocalGraphLoader {

  /** please provide your mock vertex list */
  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return Lists.newArrayList(
        constructionVertex(
            "MOCK1",
            "PE.JiuZhi",
            "shape4",
            "MULTIPOINT(116.506619 39.945368,116.509562 39.945402,116.509474 39.943348,116.506648 39"
                + ".943247)",
            "shape2",
            "LINESTRING(116.506619 39.945368,116.509562 39.945402,116.509474 39.943348,116.506648 39"
                + ".943247)",
            "id",
            "1"),
        constructionVertex(
            "MOCK2",
            "PE.JiuZhi",
            "shape4",
            "MULTIPOINT(116.506619 39.945368,116.509562 39.945402,116.509474 39.943348,116.506648 39"
                + ".943247)",
            "shape2",
            "LINESTRING(116.506619 39.945368,116.509562 39.945402,116.509474 39.943348,116.506648 39"
                + ".943247)",
            "id",
            "1"),
        constructionVertex("35f1abf9", "STD.S2CellId"),
        constructionVertex("35f1abfb", "STD.S2CellId"),
        constructionVertex("35f1abfd", "STD.S2CellId"),
        constructionVertex("35f1abff", "STD.S2CellId"));
  }

  /** please provide your mock edge list */
  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionEdge("MOCK1", "shape2S2CellId", "35f1abf9"),
        constructionEdge("MOCK1", "shape2S2CellId", "35f1abfb"),
        constructionEdge("MOCK1", "shape2S2CellId", "35f1abfd"),
        constructionEdge("MOCK1", "shape2S2CellId", "35f1abff"),
        constructionEdge("MOCK2", "shape2S2CellId", "35f1abf9"),
        constructionEdge("MOCK2", "shape2S2CellId", "35f1abfb"),
        constructionEdge("MOCK2", "shape2S2CellId", "35f1abfd"),
        constructionEdge("MOCK2", "shape2S2CellId", "35f1abff"));
  }
}
