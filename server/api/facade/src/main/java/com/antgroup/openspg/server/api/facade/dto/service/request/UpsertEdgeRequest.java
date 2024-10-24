/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.request;

import java.util.List;
import lombok.Data;

@Data
public class UpsertEdgeRequest {

  private Long projectId;
  private Boolean upsertAdjacentVertices = true;
  private List<EdgeRecordInstance> edges;
}
