package com.antgroup.openspg.server.api.facade.dto.service.request;

import lombok.Data;

@Data
public class VectorSearchRequest {
  private Long projectId;
  private final String label;
  private final String propertyKey;
  private final float[] queryVector;
  private final Integer efSearch;
  private final Integer topk;
}
