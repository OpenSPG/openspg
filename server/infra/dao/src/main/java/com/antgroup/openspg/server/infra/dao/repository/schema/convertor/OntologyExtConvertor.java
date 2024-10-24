/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.infra.dao.repository.schema.convertor;

import com.antgroup.openspg.core.schema.model.type.ExtTypeEnum;
import com.antgroup.openspg.server.core.schema.service.type.model.OntologyExt;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyExtDO;

/**
 * @author xcj01388694
 * @version OntologyExtConvertor.java, v 0.1 2024年03月06日 上午11:31 xcj01388694
 */
public class OntologyExtConvertor {

  public static OntologyExt toOntologyExt(OntologyExtDO ontologyExtDO) {
    if (null == ontologyExtDO) {
      return null;
    }
    OntologyExt ontologyExt = new OntologyExt();
    ontologyExt.setId(ontologyExtDO.getId());
    ontologyExt.setResourceId(ontologyExtDO.getResourceId());
    ontologyExt.setResourceType(ontologyExtDO.getResourceType());
    ontologyExt.setExtType(ExtTypeEnum.valueOf(ontologyExtDO.getExtType()));
    ontologyExt.setField(ontologyExtDO.getField());
    ontologyExt.setStatus(ontologyExtDO.getStatus());
    ontologyExt.setConfig(ontologyExtDO.getConfig());
    return ontologyExt;
  }
}
