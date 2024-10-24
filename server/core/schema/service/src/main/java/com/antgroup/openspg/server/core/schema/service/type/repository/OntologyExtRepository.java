/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.schema.service.type.repository;

import com.antgroup.openspg.core.schema.model.type.ExtTypeEnum;
import com.antgroup.openspg.server.core.schema.service.type.model.OntologyExt;
import java.util.List;
import java.util.Set;

/**
 * @author xcj01388694
 * @version OntologyExtRepository.java, v 0.1 2024年03月05日 下午5:53 xcj01388694
 */
public interface OntologyExtRepository {

  List<OntologyExt> getExtInfoListByIds(
      Set<String> resourceIds, String resourceType, ExtTypeEnum extType);
}
