package com.antgroup.openspg.server.api.facade.dto.schema.request;

import com.antgroup.openspg.core.schema.model.identifier.ConceptIdentifier;
import lombok.Data;

@Data
public class ConceptLevelInstanceRequest {
  private String conceptType;
  private String rootConceptInstance = ConceptIdentifier.ROOT;
}
