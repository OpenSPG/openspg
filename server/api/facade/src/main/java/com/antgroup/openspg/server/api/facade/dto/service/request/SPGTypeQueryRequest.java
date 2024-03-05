package com.antgroup.openspg.server.api.facade.dto.service.request;

import java.util.Set;
import lombok.Data;

@Data
public class SPGTypeQueryRequest {
  private String spgType;
  private Set<String> ids;
}
