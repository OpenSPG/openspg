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

package com.antgroup.openspg.reasoner.graphstate;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.generator.AbstractGraphGenerator;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import java.util.Map;
import java.util.Set;

public abstract class LoadSourceData extends AbstractGraphGenerator {

  /**
   * Load alias to vertex map from source data
   *
   * @return
   */
  public abstract Map<String, Set<IVertex<IVertexId, IProperty>>> loadAlias2Vertex();

  /**
   * Load alias to edge map from source data
   *
   * @return
   */
  public abstract Map<String, Set<IEdge<IVertexId, IProperty>>> loadAlias2Edge();

  /**
   * Load source data schema
   *
   * @return
   */
  public abstract Pattern loadPattern();
}
