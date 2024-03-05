package com.antgroup.openspg.server.api.facade.dto.service.request;

import lombok.Data;

@Data
public class RelationSamplingRequest {
  private String srcSpgType;
  private String relation;
  private String dstSpgType;
  private Integer limit;
}
