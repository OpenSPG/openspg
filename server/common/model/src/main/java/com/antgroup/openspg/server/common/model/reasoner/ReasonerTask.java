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
package com.antgroup.openspg.server.common.model.reasoner;

import com.antgroup.openspg.server.common.model.reasoner.result.Edge;
import com.antgroup.openspg.server.common.model.reasoner.result.Node;
import com.antgroup.openspg.server.common.model.reasoner.result.Path;
import com.antgroup.openspg.server.common.model.reasoner.result.TableResult;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ReasonerTask {
  private String taskId;
  private Long projectId;
  private String graphStoreUrl;
  private String dsl;
  private Map<String, String> params;
  private StatusEnum status;
  private String resultMessage;
  private TableResult resultTableResult;
  private List<Node> resultNodes;
  private List<Edge> resultEdges;
  private List<Path> resultPaths;
  private Object extend;
}
