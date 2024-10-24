/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.request;

import java.util.List;
import lombok.Data;

@Data
public class DeleteEdgeRequest {

  private Long projectId;
  private List<EdgeRecordInstance> edges;
}
