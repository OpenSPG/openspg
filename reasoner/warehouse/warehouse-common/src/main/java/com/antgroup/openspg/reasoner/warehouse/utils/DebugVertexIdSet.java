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

package com.antgroup.openspg.reasoner.warehouse.utils;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.util.HashSet;
import java.util.Set;

public class DebugVertexIdSet {
  /** vertex alias */
  public static volatile String DEBUG_VERTEX_ALIAS = null;

  /** vertex id set */
  public static volatile Set<IVertexId> DEBUG_VERTEX_ID_SET = new HashSet<>();

  /** carry vertex bizId */
  public static volatile Boolean DEBUG_CARRY_ID_PROPERTY = false;
}
