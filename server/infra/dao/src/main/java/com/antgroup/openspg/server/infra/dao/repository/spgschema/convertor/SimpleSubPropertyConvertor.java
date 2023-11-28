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

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.schema.model.BasicInfo;
import com.antgroup.openspg.schema.model.OntologyId;
import com.antgroup.openspg.schema.model.SchemaConstants;
import com.antgroup.openspg.schema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.schema.model.predicate.EncryptTypeEnum;
import com.antgroup.openspg.schema.model.type.MultiVersionConfig;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyPropertyDO;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.enums.MapTypeEnum;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.enums.PropertyCategoryEnum;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.enums.ValidStatusEnum;
import com.antgroup.openspg.server.core.schema.service.predicate.model.SimpleSubProperty;
import java.util.Date;

public class SimpleSubPropertyConvertor {

  public static OntologyPropertyDO toNewDO(SimpleSubProperty simpleSubProperty) {
    if (null == simpleSubProperty) {
      return null;
    }

    OntologyPropertyDO propertyRangeDO = new OntologyPropertyDO();
    propertyRangeDO.setGmtCreate(new Date());
    propertyRangeDO.setGmtModified(new Date());
    propertyRangeDO.setId(simpleSubProperty.getAlterId());
    propertyRangeDO.setOriginalId(simpleSubProperty.getUniqueId());
    propertyRangeDO.setVersion(SchemaConstants.DEFAULT_ONTOLOGY_VERSION);
    propertyRangeDO.setPropertyName(simpleSubProperty.getBasicInfo().getName().getName());
    propertyRangeDO.setPropertyNameZh(simpleSubProperty.getBasicInfo().getNameZh());
    propertyRangeDO.setPropertyDesc(simpleSubProperty.getBasicInfo().getDesc());
    propertyRangeDO.setPropertyDescZh(simpleSubProperty.getBasicInfo().getDesc());
    propertyRangeDO.setDomainId(simpleSubProperty.getSubjectId().getAlterId());
    propertyRangeDO.setOriginalDomainId(simpleSubProperty.getSubjectId().getUniqueId());
    propertyRangeDO.setRangeId(simpleSubProperty.getObjectId().getAlterId());
    propertyRangeDO.setOriginalRangeId(simpleSubProperty.getObjectId().getUniqueId());
    propertyRangeDO.setStatus(ValidStatusEnum.VALID.getCode());
    propertyRangeDO.setConstraintId(
        simpleSubProperty.getConstraintId() == null ? 0L : simpleSubProperty.getConstraintId());
    propertyRangeDO.setPropertyCategory(PropertyCategoryEnum.BASIC.name());
    propertyRangeDO.setMapType(
        simpleSubProperty.isFromRelation() ? MapTypeEnum.EDGE.name() : MapTypeEnum.PROP.name());
    propertyRangeDO.setStorePropertyName(propertyRangeDO.getPropertyName());
    propertyRangeDO.setTransformerId(0L);
    propertyRangeDO.setProjectId(simpleSubProperty.getProjectId());
    propertyRangeDO.setVersionStatus(AlterStatusEnum.ONLINE.name());
    propertyRangeDO.setMaskType(
        simpleSubProperty.getEncryptTypeEnum() == null
            ? EncryptTypeEnum.NONE.getType()
            : simpleSubProperty.getEncryptTypeEnum().getType());
    return propertyRangeDO;
  }

  public static OntologyPropertyDO toUpdateDO(SimpleSubProperty simpleSubProperty) {
    if (null == simpleSubProperty) {
      return null;
    }

    OntologyPropertyDO propertyRangeDO = new OntologyPropertyDO();
    propertyRangeDO.setGmtModified(new Date());
    propertyRangeDO.setId(simpleSubProperty.getAlterId());
    propertyRangeDO.setVersion(SchemaConstants.DEFAULT_ONTOLOGY_VERSION);
    propertyRangeDO.setPropertyNameZh(simpleSubProperty.getBasicInfo().getNameZh());
    propertyRangeDO.setPropertyDesc(simpleSubProperty.getBasicInfo().getDesc());
    propertyRangeDO.setPropertyDescZh(simpleSubProperty.getBasicInfo().getDesc());
    propertyRangeDO.setConstraintId(
        simpleSubProperty.getConstraintId() == null ? 0L : simpleSubProperty.getConstraintId());
    propertyRangeDO.setMaskType(
        simpleSubProperty.getEncryptTypeEnum() == null
            ? EncryptTypeEnum.NONE.getType()
            : simpleSubProperty.getEncryptTypeEnum().getType());
    return propertyRangeDO;
  }

  public static SimpleSubProperty toModel(OntologyPropertyDO propertyRangeDO) {
    BasicInfo<PredicateIdentifier> basicInfo =
        new BasicInfo<>(
            new PredicateIdentifier(propertyRangeDO.getPropertyName()),
            propertyRangeDO.getPropertyNameZh(),
            propertyRangeDO.getPropertyDesc());
    MultiVersionConfig multiVersionConfig =
        JSON.parseObject(propertyRangeDO.getMultiverConfig(), MultiVersionConfig.class);
    EncryptTypeEnum encryptTypeEnum = EncryptTypeEnum.toEnum(propertyRangeDO.getMaskType());
    Long constraintId = propertyRangeDO.getConstraintId();
    boolean fromRelation = MapTypeEnum.EDGE.name().equals(propertyRangeDO.getMapType());

    OntologyId subPropertyId =
        new OntologyId(propertyRangeDO.getOriginalId(), propertyRangeDO.getId());
    OntologyId subjectId =
        new OntologyId(propertyRangeDO.getOriginalDomainId(), propertyRangeDO.getDomainId());
    OntologyId objectId =
        new OntologyId(propertyRangeDO.getOriginalRangeId(), propertyRangeDO.getRangeId());

    SimpleSubProperty simpleSubProperty =
        new SimpleSubProperty(
            basicInfo,
            subjectId,
            objectId,
            multiVersionConfig,
            encryptTypeEnum,
            constraintId,
            fromRelation);
    simpleSubProperty.setProjectId(propertyRangeDO.getProjectId());
    simpleSubProperty.setOntologyId(subPropertyId);
    return simpleSubProperty;
  }
}
