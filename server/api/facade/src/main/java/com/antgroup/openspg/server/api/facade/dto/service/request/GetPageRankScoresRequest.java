/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class GetPageRankScoresRequest {

  private Long projectId;
  private String targetVertexType;
  private List<VertexRecordInstance> startNodes = new ArrayList<>();
}
