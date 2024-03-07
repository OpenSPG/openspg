package com.antgroup.openspg.server.api.facade.dto.schema.response;

import lombok.Data;

import java.util.Map;

@Data
public class ConceptInstanceResponse {
  private String id;
  private Map<String, Object> properties;
}
