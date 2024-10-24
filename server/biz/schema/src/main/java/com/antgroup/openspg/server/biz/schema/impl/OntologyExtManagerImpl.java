/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
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
