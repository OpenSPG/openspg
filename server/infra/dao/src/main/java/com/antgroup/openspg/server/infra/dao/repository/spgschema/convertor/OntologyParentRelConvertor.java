/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.server.infra.dao.repository.spgschema.convertor;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OntologyDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OntologyDOWithBLOBs;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OntologyParentRelDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.config.Constants;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.config.OntologyEntityName;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.ValidStatusEnum;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.YesOrNoEnum;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.type.ParentTypeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class OntologyParentRelConvertor {

  public static OntologyParentRelDO toDO(ParentTypeInfo inheritInfo) {
    if (null == inheritInfo) {
      return null;
    }

    OntologyParentRelDO ontologyParentRelDO = new OntologyParentRelDO();
    ontologyParentRelDO.setGmtCreate(new Date());
    ontologyParentRelDO.setGmtModified(new Date());
    ontologyParentRelDO.setEntityId(inheritInfo.getUniqueId());
    ontologyParentRelDO.setStatus(ValidStatusEnum.VALID.getCode());
    ontologyParentRelDO.setParentId(inheritInfo.getParentUniqueId());
    ontologyParentRelDO.setPath(
        StringUtils.join(inheritInfo.getInheritPath(), Constants.INHERIT_PATH_SEP));
    ontologyParentRelDO.setDeepInherit(YesOrNoEnum.N.name());
    return ontologyParentRelDO;
  }

  public static List<ParentTypeInfo> toModel(
      List<OntologyParentRelDO> ontologyParentRelDOS, List<OntologyDOWithBLOBs> parentDOS) {
    if (CollectionUtils.isEmpty(ontologyParentRelDOS) || CollectionUtils.isEmpty(parentDOS)) {
      return Collections.emptyList();
    }

    List<ParentTypeInfo> parentTypeInfos = new ArrayList<>();
    Map<Long, String> parentNameMap =
        parentDOS.stream()
            .collect(
                Collectors.toMap(
                    OntologyDO::getOriginalId,
                    (e -> new OntologyEntityName(e.getName()).getUniqueName())));
    ontologyParentRelDOS.forEach(
        entityParentDO -> {
          String[] inheritEntityIds = entityParentDO.getPath().split(Constants.INHERIT_PATH_SEP);
          List<Long> inheritPath =
              Arrays.stream(inheritEntityIds).map(Long::parseLong).collect(Collectors.toList());
          SPGTypeIdentifier parentTypeIdentifier =
              parentNameMap.containsKey(entityParentDO.getParentId())
                  ? SPGTypeIdentifier.parse(parentNameMap.get(entityParentDO.getParentId()))
                  : null;
          parentTypeInfos.add(
              new ParentTypeInfo(
                  entityParentDO.getEntityId(),
                  entityParentDO.getParentId(),
                  parentTypeIdentifier,
                  inheritPath));
        });
    return parentTypeInfos;
  }
}
