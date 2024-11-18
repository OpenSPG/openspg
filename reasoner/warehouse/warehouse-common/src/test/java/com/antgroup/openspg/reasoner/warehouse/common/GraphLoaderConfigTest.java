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
package com.antgroup.openspg.reasoner.warehouse.common;

import com.antgroup.openspg.reasoner.warehouse.common.config.EdgeLoaderConfig;
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig;
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphVersionConfig;
import com.antgroup.openspg.reasoner.warehouse.common.config.VertexLoaderConfig;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class GraphLoaderConfigTest {

  @Test
  public void testHash() {
    GraphLoaderConfig config = new GraphLoaderConfig();
    Set<VertexLoaderConfig> vertexLoaderConfigs = new HashSet<>();
    Set<EdgeLoaderConfig> edgeLoaderConfigs = new HashSet<>();
    config.setVertexLoaderConfigs(vertexLoaderConfigs);
    config.setEdgeLoaderConfigs(edgeLoaderConfigs);

    GraphLoaderConfig config2 = new GraphLoaderConfig();
    config2.setVertexLoaderConfigs(vertexLoaderConfigs);
    config2.setEdgeLoaderConfigs(edgeLoaderConfigs);

    Assert.assertEquals(config.hashCode(), config2.hashCode());

    VertexLoaderConfig vertexLoaderConfig1 = new VertexLoaderConfig();
    vertexLoaderConfig1.setVertexType("vertexType1");
    vertexLoaderConfigs.add(vertexLoaderConfig1);

    Assert.assertEquals(config.hashCode(), config2.hashCode());

    VertexLoaderConfig vertexLoaderConfig2 = new VertexLoaderConfig();
    vertexLoaderConfig2.setVertexType("vertexType2");
    vertexLoaderConfigs.add(vertexLoaderConfig2);

    Assert.assertEquals(config.hashCode(), config2.hashCode());

    EdgeLoaderConfig edgeLoaderConfig1 = new EdgeLoaderConfig();
    edgeLoaderConfig1.setEdgeType("vertexType1_link_vertexType2");
    edgeLoaderConfigs.add(edgeLoaderConfig1);

    Assert.assertEquals(config.hashCode(), config2.hashCode());

    EdgeLoaderConfig edgeLoaderConfig2 = new EdgeLoaderConfig();
    edgeLoaderConfig2.setEdgeType("vertexType1_link2_vertexType2");
    edgeLoaderConfigs.add(edgeLoaderConfig2);

    Assert.assertEquals(config.hashCode(), config2.hashCode());
  }

  @Test
  public void testMerge() {
    GraphLoaderConfig config = new GraphLoaderConfig();
    Set<VertexLoaderConfig> vertexLoaderConfigs = new HashSet<>();
    Set<EdgeLoaderConfig> edgeLoaderConfigs = new HashSet<>();
    config.setVertexLoaderConfigs(vertexLoaderConfigs);
    config.setEdgeLoaderConfigs(edgeLoaderConfigs);

    VertexLoaderConfig vertexLoaderConfig1 = new VertexLoaderConfig();
    vertexLoaderConfig1.setVertexType("vertexType1");
    vertexLoaderConfig1.setNeedProperties(new HashSet<>(Arrays.asList(new String[] {"id"})));
    vertexLoaderConfigs.add(vertexLoaderConfig1);

    EdgeLoaderConfig edgeLoaderConfig1 = new EdgeLoaderConfig();
    edgeLoaderConfig1.setEdgeType("vertexType1_link_vertexType2");
    edgeLoaderConfig1.setNeedProperties(new HashSet<>(Arrays.asList(new String[] {"id"})));
    edgeLoaderConfigs.add(edgeLoaderConfig1);

    GraphLoaderConfig config2 = new GraphLoaderConfig();
    Set<VertexLoaderConfig> vertexLoaderConfigs2 = new HashSet<>();
    Set<EdgeLoaderConfig> edgeLoaderConfigs2 = new HashSet<>();
    config2.setVertexLoaderConfigs(vertexLoaderConfigs2);
    config2.setEdgeLoaderConfigs(edgeLoaderConfigs2);

    VertexLoaderConfig vertexLoaderConfig2 = new VertexLoaderConfig();
    vertexLoaderConfig2.setVertexType("vertexType1");
    vertexLoaderConfig2.setNeedProperties(new HashSet<>(Arrays.asList(new String[] {"name"})));
    vertexLoaderConfigs2.add(vertexLoaderConfig2);

    EdgeLoaderConfig edgeLoaderConfig2 = new EdgeLoaderConfig();
    edgeLoaderConfig2.setEdgeType("vertexType1_link_vertexType2");
    edgeLoaderConfig2.setNeedProperties(new HashSet<>(Arrays.asList(new String[] {"name"})));
    edgeLoaderConfigs2.add(edgeLoaderConfig2);

    config = config.merge(config2);
    Assert.assertTrue(
        config
            .getVertexLoaderConfigs()
            .iterator()
            .next()
            .getNeedProperties()
            .equals(new HashSet<>(Arrays.asList(new String[] {"id", "name"}))));
    Assert.assertTrue(
        config
            .getEdgeLoaderConfigs()
            .iterator()
            .next()
            .getNeedProperties()
            .equals(new HashSet<>(Arrays.asList(new String[] {"id", "name"}))));
  }

  @Test
  public void testVersionConfig() {
    String configStr = ",0,100000000";
    GraphVersionConfig graphVersionConfig = new GraphVersionConfig(configStr);
    Assert.assertNull(graphVersionConfig.getSnapshotVersion());
    Assert.assertEquals((long) graphVersionConfig.getStartVersion(), 0L);
    Assert.assertEquals((long) graphVersionConfig.getEndVersion(), 100000000L);
  }

  @Test
  public void testVersionConfig2() {
    GraphVersionConfig graphVersionConfig = new GraphVersionConfig(10L, null, null);
    Assert.assertNull(graphVersionConfig.getStartVersion());
    Assert.assertNull(graphVersionConfig.getEndVersion());
    Assert.assertEquals((long) graphVersionConfig.getSnapshotVersion(), 10L);
  }
}
