package com.antgroup.openspg.server.biz.schema;

import com.antgroup.openspg.server.api.facade.dto.schema.request.ConceptLevelInstanceRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptInstanceResponse;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptLevelInstanceResponse;

import java.util.List;
import java.util.Set;

public interface ConceptInstanceManager {

  ConceptLevelInstanceResponse queryConceptLevelInstance(ConceptLevelInstanceRequest request);

  List<ConceptInstanceResponse> query(String conceptType, Set<String> conceptInstanceIds);
}
