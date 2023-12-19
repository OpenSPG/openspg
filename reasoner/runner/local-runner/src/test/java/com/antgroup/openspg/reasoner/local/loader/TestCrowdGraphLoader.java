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

public class TestCrowdGraphLoader extends AbstractLocalGraphLoader {
  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return Lists.newArrayList(
        constructionVertex("中国-浙江省-杭州市", "CKG.AdministrativeArea"),
        constructionVertex("中国-浙江省-温州市", "CKG.AdministrativeArea"),
        constructionVertex("bus", "TuringCore.TravelMode"),
        constructionVertex("train", "TuringCore.TravelMode"),
        constructionVertex("u1", "TuringCore.AlipayUser", "workLoc", "中国-浙江省-杭州市"),
        constructionVertex(
            "u1_te1",
            "TuringCore.TravelEvent",
            "eventTime",
            "1681208495011",
            "travelEndpoint",
            "中国-浙江省-杭州市",
            "travelMode",
            "bus"),
        constructionVertex(
            "u1_te2",
            "TuringCore.TravelEvent",
            "eventTime",
            "1681208495012",
            "travelEndpoint",
            "中国-浙江省-杭州市",
            "travelMode",
            "train"),
        constructionVertex(
            "u1_te3",
            "TuringCore.TravelEvent",
            "eventTime",
            "1681208495013",
            "travelEndpoint",
            "中国-浙江省-杭州市",
            "travelMode",
            "train"),
        constructionVertex("u2", "TuringCore.AlipayUser", "workLoc", "中国-浙江省-杭州市"),
        constructionVertex(
            "u2_te1",
            "TuringCore.TravelEvent",
            "eventTime",
            "1681208495021",
            "travelEndpoint",
            "中国-浙江省-温州市",
            "travelMode",
            "bus"),
        constructionVertex(
            "u2_te2",
            "TuringCore.TravelEvent",
            "eventTime",
            "1681208495021",
            "travelEndpoint",
            "中国-浙江省-杭州市",
            "travelMode",
            "bus"),
        constructionVertex(
            "u2_te3",
            "TuringCore.TravelEvent",
            "eventTime",
            "1681208495021",
            "travelEndpoint",
            "中国-浙江省-杭州市",
            "travelMode",
            "bus"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionEdge("u1", "workLoc", "中国-浙江省-杭州市"),
        constructionEdge("u1_te1", "traveler", "u1"),
        constructionEdge("u1_te1", "travelMode", "bus"),
        constructionEdge("u1_te1", "travelEndpoint", "中国-浙江省-杭州市"),
        constructionEdge("u1_te2", "traveler", "u1"),
        constructionEdge("u1_te2", "travelMode", "train"),
        constructionEdge("u1_te2", "travelEndpoint", "中国-浙江省-杭州市"),
        constructionEdge("u1_te3", "traveler", "u1"),
        constructionEdge("u1_te3", "travelMode", "train"),
        constructionEdge("u1_te3", "travelEndpoint", "中国-浙江省-杭州市"),
        constructionEdge("u2", "workLoc", "中国-浙江省-杭州市"),
        constructionEdge("u2_te1", "traveler", "u2"),
        constructionEdge("u2_te1", "travelMode", "bus"),
        constructionEdge("u2_te1", "travelEndpoint", "中国-浙江省-温州市"),
        constructionEdge("u2_te2", "traveler", "u2"),
        constructionEdge("u2_te2", "travelMode", "bus"),
        constructionEdge("u2_te2", "travelEndpoint", "中国-浙江省-杭州市"),
        constructionEdge("u2_te3", "traveler", "u2"),
        constructionEdge("u2_te3", "travelMode", "bus"),
        constructionEdge("u2_te3", "travelEndpoint", "中国-浙江省-杭州市"));
  }
}
