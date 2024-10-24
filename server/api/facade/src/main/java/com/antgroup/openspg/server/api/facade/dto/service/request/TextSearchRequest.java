package com.antgroup.openspg.server.api.facade.dto.service.request;

import java.util.Set;
import lombok.Data;

@Data
public class TextSearchRequest {
  private Long projectId;
  private String queryString;
  private Set<String> labelConstraints;
  private Integer topk;
}
