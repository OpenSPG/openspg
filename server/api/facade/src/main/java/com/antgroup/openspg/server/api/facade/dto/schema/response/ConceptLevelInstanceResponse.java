package com.antgroup.openspg.server.api.facade.dto.schema.response;

import java.util.List;
import lombok.Data;

@Data
public class ConceptLevelInstanceResponse {
  private String conceptType;
  private String rootConceptInstance;
  private List<ConceptInstanceResponse> children;
}
