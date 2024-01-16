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

package com.antgroup.openspg.reasoner.common;

import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;

public class PropertyTest {

  @Test
  public void testEdgePropertyClone() {
    Map<String, Object> props = new HashMap<>();
    props.put("A", 1);
    EdgeProperty edgeProperty = new EdgeProperty(props);

    EdgeProperty cloneEdgeProperty = (EdgeProperty) edgeProperty.clone();
    Assert.assertEquals(cloneEdgeProperty.get("A"), 1);

    edgeProperty.put("A", 2);
    Assert.assertEquals(cloneEdgeProperty.get("A"), 1);
    Assert.assertEquals(edgeProperty.get("A"), 2);
  }

  @Test
  public void testVertexPropertyClone() {
    Map<String, Object> props = new HashMap<>();
    props.put("A", 1);
    VertexProperty vertexProperty = new VertexProperty(props);

    VertexProperty cloneVertexProperty = (VertexProperty) vertexProperty.clone();
    Assert.assertEquals(cloneVertexProperty.get("A"), 1);

    vertexProperty.put("A", 2);
    Assert.assertEquals(cloneVertexProperty.get("A"), 1);
    Assert.assertEquals(vertexProperty.get("A"), 2);
  }

  @Test
  public void testVersionVertexPropertyClone() {

    Map<String, TreeMap<Long, Object>> props = new HashMap<>();
    TreeMap<Long, Object> versionValue = new TreeMap<>();
    versionValue.put(0L, 1);
    props.put("A", versionValue);
    VertexVersionProperty vertexProperty = new VertexVersionProperty(props);

    VertexVersionProperty cloneVertexProperty = (VertexVersionProperty) vertexProperty.clone();

    Assert.assertEquals(cloneVertexProperty.get("A"), 1);

    vertexProperty.put("A", 2);
    Assert.assertEquals(cloneVertexProperty.get("A"), 1);
    Assert.assertEquals(vertexProperty.get("A"), 2);
  }

  @Test
  public void testVersionVertexPropertyGet() {
    Map<String, TreeMap<Long, Object>> props = new HashMap<>();
    TreeMap<Long, Object> versionValue = new TreeMap<>();
    versionValue.put(1L, 1);
    versionValue.put(3L, 3);
    versionValue.put(5L, 5);
    props.put("A", versionValue);
    VertexVersionProperty vertexProperty = new VertexVersionProperty(props);

    Assert.assertEquals(5, vertexProperty.get("A"));
    Assert.assertEquals(5, vertexProperty.get("A", 5L));
    Assert.assertEquals(3, vertexProperty.get("A", 4L));
    Assert.assertEquals(3, vertexProperty.get("A", 3L));
    Assert.assertEquals(1, vertexProperty.get("A", 2L));
    Assert.assertEquals(1, vertexProperty.get("A", 1L));
    Assert.assertNull(vertexProperty.get("A", 0L));

    vertexProperty.remove("A", 1L);
    Assert.assertNull(vertexProperty.get("A", 1L));

    vertexProperty.put("A", 1, 1L);
    Assert.assertEquals(1, vertexProperty.get("A", 1L));
  }
}
