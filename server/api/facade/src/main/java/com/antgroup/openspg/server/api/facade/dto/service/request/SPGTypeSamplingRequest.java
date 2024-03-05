package com.antgroup.openspg.server.api.facade.dto.service.request;

import lombok.Data;

@Data
public class SPGTypeSamplingRequest {
  private String spgType;
  private Integer limit;
}
