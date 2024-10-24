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

package com.antgroup.openspg.server.biz.schema.impl;

import com.antgroup.openspg.core.schema.model.type.ExtTypeEnum;
import com.antgroup.openspg.server.biz.schema.OntologyExtManager;
import com.antgroup.openspg.server.core.schema.service.type.model.OntologyExt;
import com.antgroup.openspg.server.core.schema.service.type.repository.OntologyExtRepository;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xcj01388694
 * @version OntologyExtManager.java, v 0.1 2024年03月05日 下午7:05 xcj01388694
 */
@Service
public class OntologyExtManagerImpl implements OntologyExtManager {

  @Autowired private OntologyExtRepository ontologyExtRepository;

  @Override
  public List<OntologyExt> getExtInfoListByIds(
      Set<String> resourceIds, String resourceType, ExtTypeEnum extType) {
    return ontologyExtRepository.getExtInfoListByIds(resourceIds, resourceType, extType);
  }
}
