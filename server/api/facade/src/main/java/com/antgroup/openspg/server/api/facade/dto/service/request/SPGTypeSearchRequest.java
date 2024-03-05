package com.antgroup.openspg.server.api.facade.dto.service.request;

import lombok.Data;

@Data
public class SPGTypeSearchRequest {
  private String keyword;
  private Integer pageSize = 10;
  private Integer pageIdx = 0;
}
