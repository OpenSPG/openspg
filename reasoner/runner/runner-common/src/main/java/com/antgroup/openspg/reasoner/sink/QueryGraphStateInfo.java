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

package com.antgroup.openspg.reasoner.sink;

import com.antgroup.openspg.reasoner.common.graph.type.GraphItemType;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class QueryGraphStateInfo implements Serializable {
  private GraphItemType sourceGraphItemType;
  private List<String> sourceTypeList;

  private GraphItemType targetGraphItemType;
  private List<String> targetPropertyNameList;

  private List<String> targetEdgeTypeList;
  private String targetVertexIdString;
}
