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

package com.antgroup.openspg.server.common.model.job;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SubGraph extends BaseModel {

  private List<Node> resultNodes;
  private List<Edge> resultEdges;
  private String className;

  @Data
  public static class Node extends BaseModel {
    private static final long serialVersionUID = 2507408653380201214L;
    private String id;
    private String bizId;
    private String name;
    private String label;
    private Map<String, Object> properties = new HashMap<>();
  }

  @Data
  public static class Edge extends BaseModel {
    private static final long serialVersionUID = 6567121968824686072L;
    private String id;
    private String from;
    private String fromId;
    private String fromType;
    private String to;
    private String toId;
    private String toType;
    private String label;
    private Long version;
    private Map<String, Object> properties = new HashMap<>();
  }
}
