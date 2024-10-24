/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.server.infra.dao.repository.schema.convertor;

import com.antgroup.openspg.core.schema.model.type.ExtTypeEnum;
import com.antgroup.openspg.server.core.schema.service.type.model.OntologyExt;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyExtDO;

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
