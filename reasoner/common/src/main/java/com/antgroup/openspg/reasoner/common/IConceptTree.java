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

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.util.List;

public interface IConceptTree {

  List<String> getBelongToConcept(IVertexId id, String edgeType, Direction direction);
  /** get upper concept */
  String getUpper(String conceptType, String concept);

  /** get lower concept */
  List<String> getLower(String conceptType, String concept);
}
