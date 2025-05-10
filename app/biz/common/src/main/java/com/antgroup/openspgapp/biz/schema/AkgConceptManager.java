package com.antgroup.openspgapp.biz.schema;

import com.antgroup.openspgapp.biz.schema.dto.ConceptNodeDTO;
import com.antgroup.openspgapp.biz.schema.dto.ConceptTreeDTO;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/AkgConceptManager.class */
public interface AkgConceptManager {
  ConceptTreeDTO getConceptTree(Long projectId);

  ConceptNodeDTO getConceptDetail(String primaryKey, String metaType);

  ConceptTreeDTO expandConcept(String primaryKey, String metaType);
}
