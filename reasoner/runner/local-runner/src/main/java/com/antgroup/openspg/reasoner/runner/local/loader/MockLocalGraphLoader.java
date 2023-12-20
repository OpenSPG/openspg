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
import java.util.ArrayList;
import java.util.List;

public class MockLocalGraphLoader extends AbstractLocalGraphLoader {
  private String demoGraphStr;

  /**
   * mock local graph loader
   *
   * @param demoGraphStr
   */
  public MockLocalGraphLoader(String demoGraphStr) {
    this.demoGraphStr = demoGraphStr;
  }

  @Override
  public String getDemoGraph() {
    return this.demoGraphStr;
  }

  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return new ArrayList<>();
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return new ArrayList<>();
  }
}
