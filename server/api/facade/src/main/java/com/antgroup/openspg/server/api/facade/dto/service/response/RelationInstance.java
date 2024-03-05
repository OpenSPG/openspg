package com.antgroup.openspg.server.api.facade.dto.service.response;

import java.util.Map;
import lombok.Data;

@Data
public class RelationInstance {
  private String srcId;
  private String dstId;
  private String relationType;
  private Map<String, Object> properties;
}
