/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.infra.dao.repository.schema;

import com.antgroup.openspg.core.schema.model.type.ExtTypeEnum;
import com.antgroup.openspg.server.core.schema.service.type.model.OntologyExt;
import com.antgroup.openspg.server.core.schema.service.type.repository.OntologyExtRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyExtDO;
import com.antgroup.openspg.server.infra.dao.mapper.OntologyExtDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.schema.convertor.OntologyExtConvertor;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author xcj01388694
 * @version OntologyExtRepositoryImpl.java, v 0.1 2024年03月05日 下午5:53 xcj01388694
 */
@Repository
public class OntologyExtRepositoryImpl implements OntologyExtRepository {

  @Autowired private OntologyExtDOMapper ontologyExtDOMapper;

  @Override
  public List<OntologyExt> getExtInfoListByIds(
      Set<String> resourceIds, String resourceType, ExtTypeEnum extType) {
    if (CollectionUtils.isEmpty(resourceIds)) {
      return Collections.emptyList();
    }
    List<List<String>> batchResourceIds = Lists.partition(new ArrayList<>(resourceIds), 100);
    List<OntologyExtDO> ontologyExtDOList = new ArrayList<>();
    for (List<String> batch : batchResourceIds) {
      List<OntologyExtDO> list =
          ontologyExtDOMapper.getExtInfoListByIds(
              new HashSet<>(batch), resourceType, extType.name(), "common");
      if (CollectionUtils.isNotEmpty(list)) {
        ontologyExtDOList.addAll(list);
      }
    }
    if (CollectionUtils.isEmpty(ontologyExtDOList)) {
      return Collections.emptyList();
    }
    return ontologyExtDOList.stream()
        .map(OntologyExtConvertor::toOntologyExt)
        .collect(Collectors.toList());
  }
}
