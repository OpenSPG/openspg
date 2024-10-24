/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.request;

import java.util.List;
import lombok.Data;

@Data
public class UpsertVertexRequest {

  private Long projectId;
  private List<VertexRecordInstance> vertices;
}
