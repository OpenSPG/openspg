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

package com.antgroup.openspg.server.infra.dao.repository.schema.convertor;

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.OntologyId;
import com.antgroup.openspg.core.schema.model.SchemaConstants;
import com.antgroup.openspg.core.schema.model.SchemaExtInfo;
import com.antgroup.openspg.core.schema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.EncryptTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.MountedConceptConfig;
import com.antgroup.openspg.core.schema.model.predicate.PropertyGroupEnum;
import com.antgroup.openspg.core.schema.model.semantic.RuleCode;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.schema.model.type.MultiVersionConfig;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.api.facade.SchemaJsonUtils;
import com.antgroup.openspg.server.core.schema.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyPropertyDO;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.MapTypeEnum;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.PropertyCategoryEnum;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.ValidStatusEnum;
import java.util.Date;

public class SimplePropertyConvertor {

  public static OntologyPropertyDO toNewDO(SimpleProperty simpleProperty) {
    OntologyPropertyDO propertyRangeDO = new OntologyPropertyDO();
    propertyRangeDO.setGmtCreate(new Date());
    propertyRangeDO.setGmtModified(new Date());
    propertyRangeDO.setId(simpleProperty.getAlterId());
    propertyRangeDO.setOriginalId(simpleProperty.getUniqueId());
    BasicInfo<PredicateIdentifier> basicInfo = simpleProperty.getBasicInfo();
    propertyRangeDO.setPropertyName(basicInfo.getName().getName());
    propertyRangeDO.setPropertyNameZh(basicInfo.getNameZh());
    propertyRangeDO.setPropertyDesc(basicInfo.getDesc());
    propertyRangeDO.setPropertyDescZh(basicInfo.getDesc());
    propertyRangeDO.setDomainId(simpleProperty.getSubjectTypeId().getAlterId());
    propertyRangeDO.setOriginalDomainId(simpleProperty.getSubjectTypeId().getUniqueId());
    propertyRangeDO.setRangeId(simpleProperty.getObjectTypeId().getAlterId());
    propertyRangeDO.setOriginalRangeId(simpleProperty.getObjectTypeId().getUniqueId());
    propertyRangeDO.setStatus(ValidStatusEnum.VALID.getCode());

    SPGOntologyEnum type = simpleProperty.getOntologyType();
    switch (type) {
      case PROPERTY:
        propertyRangeDO.setMapType(MapTypeEnum.TYPE.name());
        propertyRangeDO.setPropertyCategory(PropertyCategoryEnum.BASIC.name());
        break;
      case RELATION:
        propertyRangeDO.setMapType(MapTypeEnum.TYPE.name());
        propertyRangeDO.setPropertyCategory(PropertyCategoryEnum.ADVANCED.name());
        break;
      default:
        throw new IllegalArgumentException("illegal type=" + type);
    }

    propertyRangeDO.setConstraintId(
        simpleProperty.getConstraintId() == null ? 0L : simpleProperty.getConstraintId());
    propertyRangeDO.setVersion(SchemaConstants.DEFAULT_ONTOLOGY_VERSION);
    propertyRangeDO.setProjectId(simpleProperty.getProjectId());
    propertyRangeDO.setVersionStatus(AlterStatusEnum.ONLINE.name());
    propertyRangeDO.setMaskType(
        simpleProperty.getEncryptTypeEnum() == null
            ? EncryptTypeEnum.NONE.getType()
            : simpleProperty.getEncryptTypeEnum().getType());
    propertyRangeDO.setMultiverConfig(
        simpleProperty.getMultiVersionConfig() == null
            ? null
            : SchemaJsonUtils.serialize(simpleProperty.getMultiVersionConfig()));
    propertyRangeDO.setStorePropertyName(basicInfo.getName().getName());
    propertyRangeDO.setPropertySource(null);
    propertyRangeDO.setTransformerId(0L);
    propertyRangeDO.setPropertyConfig(ExtConfigConvertor.getExtConfig(simpleProperty));
    return propertyRangeDO;
  }

  public static OntologyPropertyDO toUpdateDO(SimpleProperty simpleProperty) {
    OntologyPropertyDO propertyRangeDO = new OntologyPropertyDO();
    propertyRangeDO.setGmtModified(new Date());
    propertyRangeDO.setId(simpleProperty.getAlterId());
    BasicInfo<PredicateIdentifier> basicInfo = simpleProperty.getBasicInfo();
    propertyRangeDO.setPropertyNameZh(basicInfo.getNameZh());
    propertyRangeDO.setPropertyDesc(basicInfo.getDesc());
    propertyRangeDO.setPropertyDescZh(basicInfo.getDesc());

    propertyRangeDO.setConstraintId(
        simpleProperty.getConstraintId() == null ? 0L : simpleProperty.getConstraintId());
    propertyRangeDO.setVersion(SchemaConstants.DEFAULT_ONTOLOGY_VERSION);
    propertyRangeDO.setVersionStatus(AlterStatusEnum.ONLINE.name());
    propertyRangeDO.setMaskType(
        simpleProperty.getEncryptTypeEnum() == null
            ? EncryptTypeEnum.NONE.getType()
            : simpleProperty.getEncryptTypeEnum().getType());
    propertyRangeDO.setMultiverConfig(
        simpleProperty.getMultiVersionConfig() == null
            ? null
            : SchemaJsonUtils.serialize(simpleProperty.getMultiVersionConfig()));
    propertyRangeDO.setPropertyConfig(ExtConfigConvertor.getExtConfig(simpleProperty));
    return propertyRangeDO;
  }

  public static SimpleProperty toSimplePredicate(OntologyPropertyDO propertyRangeDO) {
    BasicInfo<PredicateIdentifier> basicInfo =
        new BasicInfo<>(
            new PredicateIdentifier(propertyRangeDO.getPropertyName()),
            propertyRangeDO.getPropertyNameZh(),
            propertyRangeDO.getPropertyDesc());
    SchemaExtInfo schemaExtInfo =
        SchemaJsonUtils.deserialize(propertyRangeDO.getPropertyConfig(), SchemaExtInfo.class);
    if (schemaExtInfo == null) {
      schemaExtInfo = new SchemaExtInfo();
    }

    MultiVersionConfig multiVersionConfig =
        SchemaJsonUtils.deserialize(propertyRangeDO.getMultiverConfig(), MultiVersionConfig.class);
    MountedConceptConfig mountedConceptConfig =
        ExtConfigConvertor.get(
            schemaExtInfo, SchemaConstants.MOUNT_CONCEPT_CONFIG_KEY, MountedConceptConfig.class);

    EncryptTypeEnum encryptTypeEnum = EncryptTypeEnum.toEnum(propertyRangeDO.getMaskType());
    PropertyGroupEnum propertyGroup =
        schemaExtInfo.getString(SchemaConstants.PROPERTY_GROUP_KEY) == null
            ? null
            : PropertyGroupEnum.toEnum(schemaExtInfo.getString(SchemaConstants.PROPERTY_GROUP_KEY));
    Long constraintId =
        propertyRangeDO.getConstraintId() != null && propertyRangeDO.getConstraintId() > 0L
            ? propertyRangeDO.getConstraintId()
            : null;
    String ruleId = schemaExtInfo.getString(SchemaConstants.PROPERTY_RULE_CONFIG_KEY);
    RuleCode ruleCode = StringUtils.isBlank(ruleId) ? null : new RuleCode(ruleId);
    String valueType = schemaExtInfo.getString(SchemaConstants.VALUE_TYPE_KEY);

    SPGOntologyEnum ontologyEnum = null;
    MapTypeEnum mapTypeEnum = MapTypeEnum.getEnum(propertyRangeDO.getMapType());
    PropertyCategoryEnum propertyCategoryEnum =
        PropertyCategoryEnum.getEnum(propertyRangeDO.getPropertyCategory());
    if (MapTypeEnum.TYPE.equals(mapTypeEnum)
        && PropertyCategoryEnum.BASIC.equals(propertyCategoryEnum)) {
      ontologyEnum = SPGOntologyEnum.PROPERTY;
    } else if (MapTypeEnum.TYPE.equals(mapTypeEnum)
        && PropertyCategoryEnum.ADVANCED.equals(propertyCategoryEnum)) {
      ontologyEnum = SPGOntologyEnum.RELATION;
    }

    OntologyId subjectTypeId =
        new OntologyId(propertyRangeDO.getOriginalDomainId(), propertyRangeDO.getDomainId());
    OntologyId objectTypeId =
        new OntologyId(propertyRangeDO.getOriginalRangeId(), propertyRangeDO.getRangeId());

    SimpleProperty simpleProperty =
        new SimpleProperty(
            basicInfo,
            subjectTypeId,
            objectTypeId,
            StringUtils.isBlank(valueType) ? null : SPGTypeEnum.toEnum(valueType),
            multiVersionConfig,
            mountedConceptConfig,
            encryptTypeEnum,
            propertyGroup,
            constraintId,
            ruleCode,
            ontologyEnum);
    simpleProperty.setProjectId(propertyRangeDO.getProjectId());
    simpleProperty.setOntologyId(
        new OntologyId(propertyRangeDO.getOriginalId(), propertyRangeDO.getId()));
    simpleProperty.setExtInfo(schemaExtInfo);
    return simpleProperty;
  }
}
