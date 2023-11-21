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

package com.antgroup.openspg.core.spgschema.service.alter.stage.handler;

import com.antgroup.openspg.cloudext.interfaces.repository.sequence.SequenceRepository;
import com.antgroup.openspg.core.spgschema.model.OntologyId;
import com.antgroup.openspg.core.spgschema.model.SchemaConstants;
import com.antgroup.openspg.core.spgschema.model.SchemaException;
import com.antgroup.openspg.core.spgschema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.service.alter.SchemaAlterUtils;
import com.antgroup.openspg.core.spgschema.service.alter.model.AlterInfoWrap;
import com.antgroup.openspg.core.spgschema.service.alter.model.SchemaAlterContext;
import com.antgroup.openspg.core.spgschema.service.type.SPGTypeService;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OntologyIdHandler {

  @Autowired private SequenceRepository sequenceRepository;
  @Autowired private SPGTypeService spgTypeService;

  public void handle(SchemaAlterContext context) {
    BaseSPGType rootType =
        spgTypeService.querySPGTypeByIdentifier(
            SPGTypeIdentifier.parse(SchemaConstants.ROOT_TYPE_UNIQUE_NAME));
    if (null == rootType) {
      throw SchemaException.spgTypeNotExist(SchemaConstants.ROOT_TYPE_UNIQUE_NAME);
    }

    AlterInfoWrap alterInfoWrap =
        this.buildWrap(context.getReleasedSchema(), context.getAlterSchema(), rootType);

    this.setSpgTypeId(alterInfoWrap);
    for (BaseAdvancedType advancedType : alterInfoWrap.getSortedAlterTypes()) {
      this.setPropertyId(advancedType, alterInfoWrap.getSpgTypeMap());
      this.setRelationId(advancedType, alterInfoWrap.getSpgTypeMap());
    }

    for (BaseAdvancedType advancedType : alterInfoWrap.getSortedAlterTypes()) {
      if (CollectionUtils.isNotEmpty(advancedType.getProperties())) {
        for (Property property : advancedType.getProperties()) {
          this.setPredicateSemanticId(property, alterInfoWrap.getPropertyMap());
        }
      }
      if (CollectionUtils.isNotEmpty(advancedType.getRelations())) {
        for (Relation relation : advancedType.getRelations()) {
          this.setPredicateSemanticId(relation, alterInfoWrap.getRelationMap());
        }
      }
    }
  }

  private AlterInfoWrap buildWrap(
      List<BaseSPGType> onlineTypes, List<BaseAdvancedType> alterTypes, BaseSPGType rootThing) {
    List<BaseSPGType> newSchema = SchemaAlterUtils.merge(onlineTypes, alterTypes);
    Map<SPGTypeIdentifier, BaseSPGType> spgTypeMap = new HashMap<>(10);
    Map<SPGTripleIdentifier, Property> propertyMap = new HashMap<>(50);
    Map<SPGTripleIdentifier, Relation> relationMap = new HashMap<>(50);

    spgTypeMap.put(rootThing.getBaseSpgIdentifier(), rootThing);
    for (BaseSPGType baseSpgType : newSchema) {
      spgTypeMap.put(baseSpgType.getBaseSpgIdentifier(), baseSpgType);
      if (CollectionUtils.isNotEmpty(baseSpgType.getProperties())) {
        for (Property property : baseSpgType.getProperties()) {
          propertyMap.put(property.getSpgTripleName(), property);
        }
      }

      if (CollectionUtils.isNotEmpty(baseSpgType.getRelations())) {
        for (Relation relation : baseSpgType.getRelations()) {
          relationMap.put(relation.getSpgTripleName(), relation);
        }
      }
    }

    List<BaseAdvancedType> sortedAlterTypes = this.getSortedTypes(newSchema);
    return new AlterInfoWrap(sortedAlterTypes, spgTypeMap, propertyMap, relationMap);
  }

  private List<BaseAdvancedType> getSortedTypes(List<BaseSPGType> newSchema) {
    List<BaseAdvancedType> sortedAdvancedTypes = SchemaAlterUtils.sortByInheritPath(newSchema);
    return sortedAdvancedTypes.stream()
        .filter(e -> e.getAlterOperation() != null)
        .collect(Collectors.toList());
  }

  private void setSpgTypeId(AlterInfoWrap alterSchemaWrap) {
    for (BaseAdvancedType advancedType : alterSchemaWrap.getSortedAlterTypes()) {
      if (advancedType.isCreate()) {
        Long uniqueId = this.genSequenceId();
        advancedType.setOntologyId(new OntologyId(uniqueId, uniqueId));

        boolean withCommonIndex = this.needCommonIndex(advancedType.getSpgTypeEnum());
        advancedType.addExtConfig(SchemaConstants.WITH_COMMON_INDEX, withCommonIndex);

        this.setParentTypeInfo(advancedType, alterSchemaWrap.getSpgTypeMap());
      }
    }
  }

  private void setPropertyId(
      BaseAdvancedType advancedType, Map<SPGTypeIdentifier, BaseSPGType> spgTypeMap) {
    if (CollectionUtils.isEmpty(advancedType.getProperties())) {
      return;
    }

    for (Property property : advancedType.getProperties()) {
      if (null == property.getAlterOperation()) {
        continue;
      }

      setOntologyId(advancedType, spgTypeMap, property);
    }
  }

  private void setRelationId(
      BaseAdvancedType advancedType, Map<SPGTypeIdentifier, BaseSPGType> spgTypeMap) {
    if (CollectionUtils.isEmpty(advancedType.getRelations())) {
      return;
    }

    for (Relation relation : advancedType.getRelations()) {
      if (null == relation.getAlterOperation()
          || Boolean.TRUE.equals(relation.getInherited())
          || Boolean.TRUE.equals(relation.isSemanticRelation())) {
        continue;
      }

      setOntologyId(advancedType, spgTypeMap, relation);
    }
  }

  private void setOntologyId(
      BaseAdvancedType advancedType,
      Map<SPGTypeIdentifier, BaseSPGType> spgTypeMap,
      Property property) {
    if (property.isCreate()) {
      Long uniqueId = this.genSequenceId();
      property.setOntologyId(new OntologyId(uniqueId, uniqueId));
      property.getSubjectTypeRef().setOntologyId(advancedType.getOntologyId());

      SPGTypeIdentifier objectTypeIdentifier = property.getObjectTypeRef().getBaseSpgIdentifier();
      BaseSPGType objectType = spgTypeMap.get(objectTypeIdentifier);
      if (null == objectType) {
        throw SchemaException.spgTypeNotExist(objectTypeIdentifier.toString());
      }
      property.getObjectTypeRef().setOntologyId(objectType.getOntologyId());
    }

    this.setSubPropertyId(property, spgTypeMap);
  }

  private void setSubPropertyId(Property property, Map<SPGTypeIdentifier, BaseSPGType> spgTypeMap) {
    if (CollectionUtils.isEmpty(property.getSubProperties())) {
      return;
    }

    for (SubProperty subProperty : property.getSubProperties()) {
      if (!subProperty.isCreate()) {
        continue;
      }

      Long uniqueId = this.genSequenceId();
      subProperty.setOntologyId(new OntologyId(uniqueId, uniqueId));
      subProperty.getSubjectTypeRef().setOntologyId(property.getOntologyId());

      SPGTypeIdentifier objectTypeIdentifier =
          subProperty.getObjectTypeRef().getBaseSpgIdentifier();
      BaseSPGType objectType = spgTypeMap.get(objectTypeIdentifier);
      if (null == objectType) {
        throw SchemaException.spgTypeNotExist(objectTypeIdentifier.toString());
      }
      subProperty.getObjectTypeRef().setOntologyId(objectType.getOntologyId());
    }
  }

  private void setPredicateSemanticId(
      Property property, Map<SPGTripleIdentifier, Property> propertyMap) {
    if (null == property.getAlterOperation()
        || Boolean.TRUE.equals(property.getInherited())
        || CollectionUtils.isEmpty(property.getSemantics())) {
      return;
    }

    for (PredicateSemantic semantic : property.getSemantics()) {
      if (!AlterOperationEnum.CREATE.equals(semantic.getAlterOperation())) {
        continue;
      }

      semantic.getSubjectTypeRef().setOntologyId(property.getOntologyId());
      SPGTripleIdentifier spgTripleIdentifier = semantic.getObjectTypeRef().newSpgTripleName();
      Property object = propertyMap.get(spgTripleIdentifier);
      if (null == object) {
        throw SchemaException.propertyNotExist(spgTripleIdentifier.toString());
      }
      semantic.getObjectTypeRef().setOntologyId(object.getOntologyId());
    }
  }

  private void setPredicateSemanticId(
      Relation relation, Map<SPGTripleIdentifier, Relation> relationMap) {
    if (null == relation.getAlterOperation()
        || Boolean.TRUE.equals(relation.getInherited())
        || Boolean.TRUE.equals(relation.isSemanticRelation())
        || CollectionUtils.isEmpty(relation.getAdvancedConfig().getSemantics())) {
      return;
    }

    for (PredicateSemantic semantic : relation.getAdvancedConfig().getSemantics()) {
      if (!AlterOperationEnum.CREATE.equals(semantic.getAlterOperation())) {
        continue;
      }

      semantic.getSubjectTypeRef().setOntologyId(relation.getOntologyId());
      SPGTripleIdentifier spgTripleIdentifier = semantic.getObjectTypeRef().newSpgTripleName();
      Relation object = relationMap.get(spgTripleIdentifier);
      if (null == object) {
        throw SchemaException.relationNotExist(spgTripleIdentifier.toString());
      }
      semantic.getObjectTypeRef().setOntologyId(object.getOntologyId());
    }
  }

  private Long genSequenceId() {
    return sequenceRepository.getSeqIdByTime();
  }

  private void setParentTypeInfo(
      BaseAdvancedType advancedType, Map<SPGTypeIdentifier, BaseSPGType> schemaTypeMap) {
    ParentTypeInfo parentTypeInfo = advancedType.getParentTypeInfo();
    List<Long> inheritPath = Lists.newArrayList(advancedType.getUniqueId());
    SPGTypeIdentifier parentTypeIdentifier = parentTypeInfo.getParentTypeIdentifier();

    while (!SchemaConstants.ROOT_TYPE_UNIQUE_NAME.equals(parentTypeIdentifier.toString())
        && schemaTypeMap.containsKey(parentTypeIdentifier)) {
      BaseSPGType parent = schemaTypeMap.get(parentTypeIdentifier);
      inheritPath.add(parent.getUniqueId());
      parentTypeIdentifier = parent.getParentTypeInfo().getParentTypeIdentifier();
    }
    inheritPath.add(
        schemaTypeMap
            .get(SPGTypeIdentifier.parse(SchemaConstants.ROOT_TYPE_UNIQUE_NAME))
            .getUniqueId());

    Collections.reverse(inheritPath);

    advancedType.setParentTypeInfo(
        parentTypeInfo.withNewParentInfo(
            advancedType.getUniqueId(), inheritPath.get(inheritPath.size() - 2), inheritPath));
  }

  public boolean needCommonIndex(SPGTypeEnum spgTypeEnum) {
    return SPGTypeEnum.CONCEPT_TYPE.equals(spgTypeEnum);
  }
}
