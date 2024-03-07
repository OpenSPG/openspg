package com.antgroup.openspg.server.biz.schema;

import com.antgroup.openspg.server.api.facade.dto.schema.request.ConceptLevelInstanceRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptLevelInstanceResponse;

public interface ConceptInstanceManager {

  ConceptLevelInstanceResponse queryConceptLevelInstance(ConceptLevelInstanceRequest request);
}
