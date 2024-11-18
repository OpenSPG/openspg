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
package com.antgroup.openspg.server.common.model.reasoner.result;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.List;

public class GraphResult extends BaseModel {
  private static final long serialVersionUID = 6567121968824686072L;

  private List<Node> nodeList;
  private List<Edge> edgeList;
  private List<Path> pathList;

  public List<Node> getNodeList() {
    return nodeList;
  }

  public void setNodeList(List<Node> nodeList) {
    this.nodeList = nodeList;
  }

  public List<Edge> getEdgeList() {
    return edgeList;
  }

  public void setEdgeList(List<Edge> edgeList) {
    this.edgeList = edgeList;
  }

  public List<Path> getPathList() {
    return pathList;
  }

  public void setPathList(List<Path> pathList) {
    this.pathList = pathList;
  }
}
