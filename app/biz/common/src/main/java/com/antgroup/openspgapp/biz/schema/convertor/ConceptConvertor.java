package com.antgroup.openspgapp.biz.schema.convertor;

import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptInstanceResponse;
import com.antgroup.openspgapp.biz.schema.dto.ConceptDTO;
import com.antgroup.openspgapp.biz.schema.dto.ConceptNodeDTO;
import java.util.Map;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/convertor/ConceptConvertor.class */
public class ConceptConvertor {
  public static ConceptNodeDTO toConceptNodeDTO(ConceptInstanceResponse instanceResponse) {
    if (null == instanceResponse) {
      return null;
    }
    ConceptNodeDTO conceptNodeDTO = new ConceptNodeDTO();
    conceptNodeDTO.setId(instanceResponse.getId());
    conceptNodeDTO.setPrimaryKey(instanceResponse.getId());
    Map<String, Object> properties = instanceResponse.getProperties();
    if (null != properties) {
      conceptNodeDTO.setName((String) properties.get("name"));
      conceptNodeDTO.setNameZh((String) properties.get("nameZh"));
      conceptNodeDTO.setDescription((String) properties.get("description"));
      conceptNodeDTO.setProperties(properties);
    }
    return conceptNodeDTO;
  }

  public static ConceptDTO toConceptDTO(ConceptInstanceResponse instanceResponse) {
    if (null == instanceResponse) {
      return null;
    }
    ConceptDTO conceptDTO = new ConceptDTO();
    conceptDTO.setId(instanceResponse.getId());
    conceptDTO.setPrimaryKey(instanceResponse.getId());
    Map<String, Object> properties = instanceResponse.getProperties();
    if (null != properties) {
      conceptDTO.setName((String) properties.get("name"));
      conceptDTO.setNameZh((String) properties.get("nameZh"));
      conceptDTO.setConceptDescription((String) properties.get("description"));
      conceptDTO.setConceptParentType((String) properties.get("conceptParentType"));
      conceptDTO.setProperties(properties);
    }
    return conceptDTO;
  }
}
