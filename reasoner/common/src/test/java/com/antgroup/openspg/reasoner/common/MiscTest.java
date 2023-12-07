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


package com.antgroup.openspg.reasoner.common;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.exception.IllegalArgumentException;
import com.antgroup.openspg.reasoner.common.graph.edge.SPO;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.PathEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

/**
 * @author donghai.ydh
 * @version MiscTest.java, v 0.1 2023-10-13 18:02 donghai.ydh
 */
public class MiscTest {

  @Test
  public void testSPO() {
    SPO spo = new SPO(null);
    Assert.assertNull(spo.getS());
    Assert.assertNull(spo.getP());
    Assert.assertNull(spo.getO());

    try {
      new SPO("");
      Assert.fail();
    } catch (Throwable e) {
      Assert.assertTrue(true);
    }

    try {
      new SPO("a_b");
      Assert.fail();
    } catch (Throwable e) {
      Assert.assertTrue(true);
    }
  }

  @Test
  public void testGetVertexVersionProperty() {
    String idString = "201#7bde28e60eaf56fd481589693019";
    Map<String, TreeMap<Long, Object>> props1 = new HashMap<>();
    TreeMap<Long, Object> idValue1 = new TreeMap<>();
    props1.put("id", idValue1);
    idValue1.put(1696348800000L, idString);
    IProperty property = new VertexVersionProperty(props1);
    for (String key : property.getKeySet()) {
      Object getValue = property.get(key);
      Assert.assertEquals(getValue, idString);
    }
  }

  @Test
  public void testTreeMapGet() {
    TreeMap<Long, Object> versionValueMap = new TreeMap<>();
    versionValueMap.put(1697126400000L, "xx");
    Object v = PropertyUtil.getVersionValue(null, versionValueMap);
    // System.out.println(v);
    Assert.assertEquals("xx", v);
  }

  static VertexVersionProperty p1;
  static VertexVersionProperty p2;

  static {
    Map<String, TreeMap<Long, Object>> props = new HashMap<>();
    TreeMap<Long, Object> versionValueMap = new TreeMap<>();
    versionValueMap.put(0L, null);
    versionValueMap.put(1697126400000L, "201#7bde28e60eaf56fd481588431762");
    props.put("id", versionValueMap);
    p1 = new VertexVersionProperty(props);

    Map<String, TreeMap<Long, Object>> props2 = new HashMap<>();
    TreeMap<Long, Object> versionValueMap2 = new TreeMap<>();
    versionValueMap2.put(0L, null);
    versionValueMap2.put(1L, null);
    versionValueMap2.put(1697385600000L, "201#7bde28e60eaf56fd481590113440");
    props2.put("id", versionValueMap2);
    p2 = new VertexVersionProperty(props2);
  }

  @Test
  public void testTreeMapGet2() {
    System.out.println(p1);
    System.out.println(p2);
    List<Tuple2<Long, Object>> getListResult = p2.get("id", 0L, 1L);
    Assert.assertEquals(2, getListResult.size());
    List<Thread> threadList = new ArrayList<>();
    for (int i = 0; i < 1000; ++i) {
      Thread thread =
          new Thread(
              new Runnable() {
                @Override
                public void run() {
                  Object v = p1.get("id");
                  Assert.assertEquals("201#7bde28e60eaf56fd481588431762", v);
                  Assert.assertEquals("201#7bde28e60eaf56fd481590113440", p2.get("id"));
                }
              });
      threadList.add(thread);
    }
    for (Thread thread : threadList) {
      thread.run();
    }
    for (Thread thread : threadList) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Test
  public void testContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("id", null);
    context.put("__id__", 1);
    System.out.println(JSON.toJSONString(context));
  }

  @Test
  public void PathEdgeTest() {
    Edge<IVertexId, IProperty> edge = new Edge<>(IVertexId.from(0L, "A"), IVertexId.from(1L, "B"));
    PathEdge<IVertexId, IProperty, IProperty> pathEdge = new PathEdge<>(edge);
    pathEdge.setValue(null);

    Vertex<IVertexId, IProperty> errorVertex = new Vertex<>(IVertexId.from(9999L, "C"));
    Edge<IVertexId, IProperty> edge2 = new Edge<>(IVertexId.from(1L, "B"), IVertexId.from(2L, "C"));
    try {
      new PathEdge<>(pathEdge, errorVertex, edge2);
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(true);
    }

    Vertex<IVertexId, IProperty> vertex = new Vertex<>(IVertexId.from(1L, "B"));
    Edge<IVertexId, IProperty> errorEdge =
        new Edge<>(IVertexId.from(9999L, "C"), IVertexId.from(2L, "B"));
    try {
      new PathEdge<>(pathEdge, vertex, errorEdge);
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(true);
    }

    PathEdge<IVertexId, IProperty, IProperty> pathEdge2 = new PathEdge<>(pathEdge, vertex, edge2);
    PathEdge<IVertexId, IProperty, IProperty> pathEdge3 = new PathEdge<>(pathEdge, vertex, edge2);

    Assert.assertFalse(pathEdge2.equals(pathEdge));
    Assert.assertEquals(pathEdge2, pathEdge3);

    PathEdge<IVertexId, IProperty, IProperty> pathEdge4 = pathEdge3.clone();
    Assert.assertEquals(pathEdge3, pathEdge4);

    pathEdge4.setSourceId(IVertexId.from(99L, "D"));
    Assert.assertFalse(pathEdge3.equals(pathEdge4));

    PathEdge<IVertexId, IProperty, IProperty> pathEdge5 = pathEdge3.clone();
    pathEdge5.setTargetId(IVertexId.from(99L, "D"));
    Assert.assertFalse(pathEdge3.equals(pathEdge5));
  }
}
