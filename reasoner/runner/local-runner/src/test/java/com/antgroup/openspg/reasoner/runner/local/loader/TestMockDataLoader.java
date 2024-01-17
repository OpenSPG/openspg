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

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.google.common.collect.Sets;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;

public class TestMockDataLoader {
  @Test
  public void test() {
    String demoGraph =
        "Graph {\n"
            + "  C_37091 [CRO.Company, regNo='C_37091']\n"
            + "  C_7661 [CRO.Company, regNo='C_7661']\n"
            + "  C_8125 [CRO.Company, regNo='C_8125'] \n"
            + "  P1 [CRO.Person]\n"
            + "  P2 [CRO.Person]\n"
            + "  P3 [CRO.Person]\n"
            + "\n"
            + "\n"
            + "  P1->C_37091[corporate]\n"
            + "  P1->C_7661 [control]\n"
            + "  P1->C_7661 [superviseDirctor]\n"
            + "\n"
            + "  P2->C_7661[corporate]\n"
            + "  P2->C_7661 [control]\n"
            + "  P2->C_7661 [superviseDirctor]\n"
            + "\n"
            + "  P3->C_8125[corporate]\n"
            + "  P3->C_7661[superviseDirctor]\n"
            + "\n"
            + "}";
    MockLocalGraphLoader mockLocalGraphLoader = new MockLocalGraphLoader(demoGraph);
    mockLocalGraphLoader.setGraphState(new MemGraphState());
    mockLocalGraphLoader.load();
    Assert.assertEquals(0, mockLocalGraphLoader.genVertexList().size());
    Assert.assertEquals(0, mockLocalGraphLoader.genEdgeList().size());
  }

  @Test
  public void test2() {
    String demoGraph =
        "Graph {\n"
            + "  C_37091 [CRO.Gang,regNo='C_37091']\n"
            + "\n"
            + "\n"
            + "  P1->C_37091[member, __from_id_type__='CRO.User', __to_id_type__='CRO.Gang']\n"
            + "\n"
            + "}";
    MemGraphState memGraphState = new MemGraphState();
    MockLocalGraphLoader mockLocalGraphLoader = new MockLocalGraphLoader(demoGraph);
    mockLocalGraphLoader.setGraphState(memGraphState);
    mockLocalGraphLoader.load();
    Assert.assertTrue(memGraphState.getVertex(IVertexId.from("C_37091", "CRO.Gang"), null) != null);
    Iterator<IEdge<IVertexId, IProperty>> it =
        memGraphState.getEdgeIterator(Sets.newHashSet("CRO.User_member_CRO.Gang"));
    Assert.assertTrue(it.hasNext());
    IEdge<IVertexId, IProperty> edge = it.next();
    Assert.assertTrue(edge.getDirection().equals(Direction.IN));
    Assert.assertTrue(edge.getTargetId().equals(IVertexId.from("P1", "CRO.User")));
    Assert.assertTrue(edge.getValue().get(Constants.EDGE_FROM_ID_KEY).equals("P1"));
  }

  @Test
  public void test3() {
    String demoGraph =
        "Graph {\n"
            + "  C_37091 [CRO.Gang,regNo='C_37091']\n"
            + "\n"
            + "\n"
            + "  P1->C_37091[member]\n"
            + "\n"
            + "}";
    MemGraphState memGraphState = new MemGraphState();
    MockLocalGraphLoader mockLocalGraphLoader = new MockLocalGraphLoader(demoGraph);
    mockLocalGraphLoader.setGraphState(memGraphState);
    try {
      mockLocalGraphLoader.load();
      Assert.fail();
    } catch (Exception e) {
      Assert.assertTrue(e.getMessage().contains("pls set this value"));
    }
  }

  @Test
  public void test4() {
    String demoGraph =
        "Graph {\n"
            + "  C_37093,C_37091 [CRO.Gang,regNo='C_37091']\n"
            + " P3 [CRO.User]"
            + "\n"
            + "\n"
            + "  P1->C_37091[member, __from_id_type__='CRO.User', __to_id_type__='CRO.Gang']\n"
            + "  C_37091->P2[member, __from_id_type__='CRO.Gang', __to_id_type__='CRO.User']\n"
            + "  C_37093->P3[member, __from_id_type__='CRO.Gang', __to_id_type__='CRO.User']\n"
            + "\n"
            + "}";
    MemGraphState memGraphState = new MemGraphState();
    MockLocalGraphLoader mockLocalGraphLoader = new MockLocalGraphLoader(demoGraph);
    mockLocalGraphLoader.setGraphState(memGraphState);
    mockLocalGraphLoader.load();
    Assert.assertTrue(memGraphState.getVertex(IVertexId.from("C_37091", "CRO.Gang"), null) != null);
    Iterator<IEdge<IVertexId, IProperty>> it =
        memGraphState.getEdgeIterator(Sets.newHashSet("CRO.User_member_CRO.Gang"));
    Assert.assertTrue(it.hasNext());
    IEdge<IVertexId, IProperty> edge = it.next();
    Assert.assertTrue(edge.getDirection().equals(Direction.IN));
    Assert.assertTrue(edge.getTargetId().equals(IVertexId.from("P1", "CRO.User")));
    Assert.assertTrue(edge.getValue().get(Constants.EDGE_FROM_ID_KEY).equals("P1"));
  }
}
