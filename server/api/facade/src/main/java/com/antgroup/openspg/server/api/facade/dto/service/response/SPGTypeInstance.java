package com.antgroup.openspg.server.api.facade.dto.service.response;

import java.util.Map;
import lombok.Data;

@Data
public class SPGTypeInstance {
  private String id;
  private String spgType;
  private Map<String, Object> properties;
}
