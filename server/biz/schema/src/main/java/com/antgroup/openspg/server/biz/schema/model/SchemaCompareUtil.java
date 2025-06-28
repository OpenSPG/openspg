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
package com.antgroup.openspg.server.biz.schema.model;

import static com.antgroup.openspg.reasoner.common.constants.Constants.OPEN_CONCEPT_HYPERNYM_PREDICATE;

import com.antgroup.openspg.common.util.enums.AdvancedTypeEnum;
import com.antgroup.openspg.common.util.enums.BasicTypeEnum;
import com.antgroup.openspg.common.util.exception.SpgException;
import com.antgroup.openspg.common.util.exception.message.SpgMessageEnum;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.SchemaExtInfo;
import com.antgroup.openspg.core.schema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.IndexTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.schema.model.predicate.PropertyGroupEnum;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.predicate.SubProperty;
import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.schema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.schema.model.type.ConceptLayerConfig;
import com.antgroup.openspg.core.schema.model.type.ConceptType;
import com.antgroup.openspg.core.schema.model.type.EntityType;
import com.antgroup.openspg.core.schema.model.type.EventType;
import com.antgroup.openspg.core.schema.model.type.IndexType;
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.schema.model.type.SPGTypeAdvancedConfig;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.server.core.schema.service.type.model.BuiltInPropertyEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/** schema compare util */
@Slf4j
public class SchemaCompareUtil {

  /**
   * compare schema change
   *
   * @param namespace
   * @param oldTypes
   * @param newTypes
   * @return SchemaChangeDTO
   */
  public SchemaChangeDTO compare(
      String namespace, List<NodeTypeModel> oldTypes, List<NodeTypeModel> newTypes) {
    List<BaseAdvancedType> addAdvancedType = new ArrayList<>();
    List<BaseAdvancedType> updateAdvancedType = new ArrayList<>();
    List<BaseAdvancedType> deleteAdvancedType = new ArrayList<>();
    SchemaChangeDTO result =
        new SchemaChangeDTO(addAdvancedType, updateAdvancedType, deleteAdvancedType);
    List<String> oldTypeNames = Lists.newArrayList();
    List<String> newTypeNames = Lists.newArrayList();
    Map<String, NodeTypeModel> oldMap = Maps.newHashMap();
    Map<String, NodeTypeModel> newMap = Maps.newHashMap();
    Map<String, String> name2Type = Maps.newHashMap();
    if (CollectionUtils.isEmpty(newTypes)) {
      convertToAddOrDelete(
          namespace, oldTypes, deleteAdvancedType, name2Type, AlterOperationEnum.DELETE);
      return result;
    }
    newTypes.forEach(
        n -> {
          newTypeNames.add(n.getName());
          newMap.put(n.getName(), n);
          name2Type.put(n.getName(), n.getType());
        });

    List<String> intersection = new ArrayList<>(newTypeNames);
    List<String> newCreate = new ArrayList<>(newTypeNames);
    List<NodeTypeModel> delete = new ArrayList<>();
    List<NodeTypeModel> create = new ArrayList<>();
    if (CollectionUtils.isEmpty(oldTypes)) {
      convertToAddOrDelete(
          namespace, newTypes, addAdvancedType, name2Type, AlterOperationEnum.CREATE);
      return result;
    }
    oldTypes.forEach(
        o -> {
          oldTypeNames.add(o.getName());
          oldMap.put(o.getName(), o);
          name2Type.put(o.getName(), o.getType());
          // 获取新增和删除的类型
          newCreate.remove(o.getName());
          if (!newTypeNames.contains(o.getName())) {
            delete.add(o);
          }
        });
    intersection.retainAll(oldTypeNames);
    for (String s : newCreate) {
      if (!oldTypeNames.contains(s)) {
        create.add(newMap.get(s));
      }
    }
    for (String s : intersection) {
      NodeTypeModel o = oldMap.get(s);
      NodeTypeModel n = newMap.get(s);
      convertToUpdate(namespace, o, n, name2Type, updateAdvancedType);
    }
    convertToAddOrDelete(
        namespace, delete, deleteAdvancedType, name2Type, AlterOperationEnum.DELETE);
    convertToAddOrDelete(namespace, create, addAdvancedType, name2Type, AlterOperationEnum.CREATE);
    return result;
  }

  /**
   * update property
   *
   * @param namespace
   * @param o
   * @param n
   * @param updateAdvancedType
   */
  private static void convertToUpdate(
      String namespace,
      NodeTypeModel o,
      NodeTypeModel n,
      Map<String, String> name2Type,
      List<BaseAdvancedType> updateAdvancedType) {
    AdvancedTypeEnum nType = AdvancedTypeEnum.valueOf(n.getType());
    AdvancedTypeEnum oType = AdvancedTypeEnum.valueOf(o.getType());
    if (!AdvancedTypeEnum.canConvert(oType, nType)) {
      log.warn("update node type, nodeName={}, oldType={}", n.getName(), o.getType());
      throw new SpgException(SpgMessageEnum.SCHEMA_CHANGE_NODE_TYPE);
    }
    SPGTypeIdentifier spgTypeIdentifier = new SPGTypeIdentifier(namespace, n.getName());
    BasicInfo basicInfo = new BasicInfo<>(spgTypeIdentifier, n.getNameZh(), n.getDesc());
    ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING;
    if (StringUtils.isNotBlank(n.getParentName())) {
      parentTypeInfo =
          new ParentTypeInfo(null, null, SPGTypeIdentifier.parse(n.getParentName()), null);
    }
    SPGTypeAdvancedConfig spgTypeAdvancedConfig = new SPGTypeAdvancedConfig();
    List<PropertyModel> props = n.getProperties();
    AdvancedTypeEnum advancedTypeEnum = AdvancedTypeEnum.valueOf(n.getType());
    BaseAdvancedType type;
    switch (advancedTypeEnum) {
      case ENTITY_TYPE:
        type = new EntityType(basicInfo, parentTypeInfo, null, null, spgTypeAdvancedConfig);
        break;
      case EVENT_TYPE:
        type = new EventType(basicInfo, parentTypeInfo, null, null, spgTypeAdvancedConfig);
        break;
      case INDEX_TYPE:
        type = new IndexType(basicInfo, parentTypeInfo, null, null, spgTypeAdvancedConfig);
        break;
      case CONCEPT_TYPE:
        if (!o.getHypernymPredicate().equalsIgnoreCase(n.getHypernymPredicate())) {
          log.warn(
              "update node type, nodeName={}, oldHypernymPredicate={}, newHypernymPredicate={}",
              n.getName(),
              o.getHypernymPredicate(),
              n.getHypernymPredicate());
          throw new SpgException(SpgMessageEnum.SCHEMA_CHANGE_HYPERNYM_PREDICATE);
        }
        type =
            new ConceptType(
                basicInfo,
                parentTypeInfo,
                null,
                null,
                spgTypeAdvancedConfig,
                new ConceptLayerConfig(n.getHypernymPredicate(), null),
                null,
                null);
        break;
      default:
        throw new RuntimeException("unsupported advanced type: " + n.getType());
    }
    type.setExtInfo(new SchemaExtInfo());
    updateProperty(namespace, o, props, name2Type, type);
    updateRelation(namespace, o, n.getRelations(), name2Type, type);
    type.setOntologyId(o.getOntologyId());
    type.setAlterOperation(AlterOperationEnum.UPDATE);
    updateAdvancedType.add(type);
  }

  /**
   * update Relation
   *
   * @param namespace
   * @param o old node type
   * @param relations new node type relations
   * @param advancedType final advanced type
   */
  private static void updateRelation(
      String namespace,
      NodeTypeModel o,
      List<EdgeTypeModel> relations,
      Map<String, String> name2Type,
      BaseAdvancedType advancedType) {
    if (CollectionUtils.isEmpty(relations) && CollectionUtils.isEmpty(o.getRelations())) {
      return;
    }
    List<Relation> relationList = Lists.newArrayList();
    advancedType.setRelations(relationList);
    List<EdgeTypeModel> oldRelations = o.getRelations();
    Map<String, EdgeTypeModel> oldRelationMap = Maps.newHashMap();
    List<String> oldRelationSPO = Lists.newArrayList();
    if (CollectionUtils.isNotEmpty(oldRelations)) {
      for (EdgeTypeModel oldRelation : oldRelations) {
        String spo =
            String.format(
                "%s_%s_%s", o.getName(), oldRelation.getName(), oldRelation.getTargetType());
        oldRelationMap.put(spo, oldRelation);
        oldRelationSPO.add(spo);
      }
    }
    List<String> delete = new ArrayList<>(oldRelationSPO);
    if (CollectionUtils.isNotEmpty(relations) && !(advancedType instanceof ConceptType)) {
      relationCreateOrUpdate(
          namespace, o, relations, advancedType, oldRelationMap, delete, relationList, name2Type);
    }
    if (CollectionUtils.isEmpty(delete)) {
      return;
    }
    delete.forEach(
        spo -> {
          EdgeTypeModel typeModel = oldRelationMap.get(spo);
          SPGTypeRef targetType = getTargetType(typeModel.getTargetType(), namespace, name2Type);
          Relation relation =
              new Relation(
                  new BasicInfo<>(
                      new PredicateIdentifier(typeModel.getName()),
                      typeModel.getNameZh(),
                      typeModel.getDesc()),
                  advancedType.toRef(),
                  targetType,
                  false,
                  new PropertyAdvancedConfig(),
                  false);
          relation.setOntologyId(typeModel.getOntologyId());
          boolean semanticRelation =
              typeModel.getSemanticRelation() == null ? false : typeModel.getSemanticRelation();
          boolean isConceptRelation =
              advancedType instanceof ConceptType
                  && OPEN_CONCEPT_HYPERNYM_PREDICATE.contains(typeModel.getName());
          if (isConceptRelation || semanticRelation) {
            relation.setAlterOperation(null);
          } else {
            relation.setAlterOperation(AlterOperationEnum.DELETE);
          }
          relationList.add(relation);
        });
  }

  /**
   * relation create or update
   *
   * @param namespace
   * @param o
   * @param relations
   * @param advancedType
   * @param oldRelationMap
   * @param delete
   * @param relationList
   */
  private static void relationCreateOrUpdate(
      String namespace,
      NodeTypeModel o,
      List<EdgeTypeModel> relations,
      BaseAdvancedType advancedType,
      Map<String, EdgeTypeModel> oldRelationMap,
      List<String> delete,
      List<Relation> relationList,
      Map<String, String> name2Type) {
    for (EdgeTypeModel edgeTypeModel : relations) {
      String spo =
          String.format(
              "%s_%s_%s", o.getName(), edgeTypeModel.getName(), edgeTypeModel.getTargetType());
      SPGTypeRef spgTypeRef = getTargetType(edgeTypeModel.getTargetType(), namespace, name2Type);
      Relation relation =
          new Relation(
              new BasicInfo<>(
                  new PredicateIdentifier(edgeTypeModel.getName()),
                  edgeTypeModel.getNameZh(),
                  edgeTypeModel.getDesc()),
              advancedType.toRef(),
              spgTypeRef,
              false,
              new PropertyAdvancedConfig(),
              false);
      relation.setExtInfo(new SchemaExtInfo());
      EdgeTypeModel typeModel = oldRelationMap.get(spo);
      if ((typeModel != null && CollectionUtils.isNotEmpty(typeModel.getProperties()))
          || CollectionUtils.isNotEmpty(edgeTypeModel.getProperties())) {
        updateRelationProperty(typeModel, edgeTypeModel.getProperties(), relation);
      }
      if (typeModel == null) {
        relation.getAdvancedConfig().setLogicalRule(setLogicalRule(null, edgeTypeModel.getRule()));
        relation.setAlterOperation(AlterOperationEnum.CREATE);
      } else {
        delete.remove(spo);
        relation
            .getAdvancedConfig()
            .setLogicalRule(setLogicalRule(typeModel.getRule(), edgeTypeModel.getRule()));
        relation.setOntologyId(typeModel.getOntologyId());
        relation.setAlterOperation(AlterOperationEnum.UPDATE);
      }
      relationList.add(relation);
    }
  }

  /**
   * update property
   *
   * @param namespace
   * @param o old node type
   * @param props new properties
   * @param advancedType
   */
  private static void updateProperty(
      String namespace,
      NodeTypeModel o,
      List<PropertyModel> props,
      Map<String, String> name2Type,
      BaseAdvancedType advancedType) {
    List<Property> propertyList = Lists.newArrayList();
    Map<String, PropertyModel> oldPropMap = Maps.newHashMap();
    List<String> oldPro = Lists.newArrayList();
    List<PropertyModel> oldProps = o.getProperties();
    for (PropertyModel oldProp : oldProps) {
      oldPropMap.put(oldProp.getName(), oldProp);
      oldPro.add(oldProp.getName());
    }
    props = CollectionUtils.isEmpty(props) ? Lists.newArrayList() : props;
    List<String> deleteProps = new ArrayList<>(oldPro);
    for (PropertyModel newProp : props) {
      PropertyModel oldProp = oldPropMap.get(newProp.getName());
      if (oldProp != null && !oldProp.getType().equals(newProp.getType())) {
        log.warn(
            "update property type, propName={}, oldType={}", newProp.getName(), oldProp.getType());
        throw new SpgException(SpgMessageEnum.SCHEMA_CHANGE_PROPERTY_TYPE);
      }
      SPGTypeRef propRef = getTargetType(newProp.getType(), namespace, name2Type);
      boolean isInherited = oldProp == null ? false : isInherited(oldProp);
      Property property =
          new Property(
              new BasicInfo<>(
                  new PredicateIdentifier(newProp.getName()),
                  newProp.getNameZh(),
                  StringUtils.EMPTY),
              advancedType.toRef(),
              propRef,
              isInherited,
              new PropertyAdvancedConfig().setIndexType(getIndexType(newProp.getIndex())));
      buildEventProperty(advancedType, property);
      property.getAdvancedConfig().setConstraint(newProp.getConstraint());
      if (oldProp == null) {
        property.getAdvancedConfig().setLogicalRule(setLogicalRule(null, newProp.getRule()));
        property.setAlterOperation(
            advancedType instanceof ConceptType ? null : AlterOperationEnum.CREATE);
      } else {
        property
            .getAdvancedConfig()
            .setLogicalRule(setLogicalRule(oldProp.getRule(), newProp.getRule()));
        deleteProps.remove(newProp.getName());
        property.setOntologyId(oldProp.getOntologyId());
        property.setAlterOperation(AlterOperationEnum.UPDATE);
      }
      propertyList.add(property);
    }
    Set<String> builtInPropertyNames = new HashSet<>();
    if (advancedType instanceof ConceptType) {
      builtInPropertyNames = BuiltInPropertyEnum.getBuiltInPropertyName(SPGTypeEnum.CONCEPT_TYPE);
    } else if (advancedType instanceof EventType) {
      builtInPropertyNames = BuiltInPropertyEnum.getBuiltInPropertyName(SPGTypeEnum.EVENT_TYPE);
    }
    for (String name : deleteProps) {
      PropertyModel oldProp = oldPropMap.get(name);
      SPGTypeRef propRef = getTargetType(oldProp.getType(), namespace, name2Type);
      boolean isInherited = isInherited(oldProp);
      Property property =
          new Property(
              new BasicInfo<>(new PredicateIdentifier(name), name, StringUtils.EMPTY),
              advancedType.toRef(),
              propRef,
              isInherited,
              new PropertyAdvancedConfig());
      if (!isInherited && !builtInPropertyNames.contains(name)) {
        property.setAlterOperation(AlterOperationEnum.DELETE);
      }
      property.getAdvancedConfig().setLogicalRule(oldProp.getRule());
      property.setOntologyId(oldProp.getOntologyId());
      propertyList.add(property);
    }
    advancedType.setProperties(propertyList);
  }

  /**
   * update relation property
   *
   * @param oldEdge
   * @param properties new edge properties
   * @param relation
   */
  private static void updateRelationProperty(
      EdgeTypeModel oldEdge, List<PropertyModel> properties, Relation relation) {
    if (CollectionUtils.isEmpty(properties)
        && (oldEdge == null || CollectionUtils.isEmpty(oldEdge.getProperties()))) {
      return;
    }
    List<SubProperty> subProperties = Lists.newArrayList();
    relation.getAdvancedConfig().setSubProperties(subProperties);
    Map<String, PropertyModel> oldsubPropMap = Maps.newHashMap();
    List<String> oldSubProp = Lists.newArrayList();
    if (oldEdge != null && CollectionUtils.isNotEmpty(oldEdge.getProperties())) {
      List<PropertyModel> oldProps = oldEdge.getProperties();
      for (PropertyModel oldProp : oldProps) {
        oldsubPropMap.put(oldProp.getName(), oldProp);
        oldSubProp.add(oldProp.getName());
      }
    }
    List<String> deleteProps = new ArrayList<>(oldSubProp);
    if (CollectionUtils.isNotEmpty(properties)) {
      relationPropertyCreateOrUpdate(
          properties, relation, oldsubPropMap, deleteProps, subProperties);
    }
    if (CollectionUtils.isEmpty(deleteProps)) {
      return;
    }
    deleteProps.forEach(
        name -> {
          PropertyModel oldProp = oldsubPropMap.get(name);
          SubProperty subProperty =
              new SubProperty(
                  new BasicInfo<>(
                      new PredicateIdentifier(oldProp.getName()),
                      oldProp.getNameZh(),
                      StringUtils.EMPTY),
                  relation.toRef(),
                  new SPGTypeRef(
                      new BasicInfo<>(
                          new SPGTypeIdentifier(null, BasicTypeEnum.TEXT.getCode()),
                          BasicTypeEnum.TEXT.getDesc(),
                          BasicTypeEnum.TEXT.getDesc()),
                      SPGTypeEnum.BASIC_TYPE),
                  new PropertyAdvancedConfig());
          subProperty.setOntologyId(oldProp.getOntologyId());
          if (oldProp.getInherited() == null || !oldProp.getInherited()) {
            subProperty.setAlterOperation(AlterOperationEnum.DELETE);
          }
          subProperties.add(subProperty);
        });
  }

  /**
   * update relation property
   *
   * @param properties
   * @param relation
   * @param oldsubPropMap
   * @param deleteProps
   * @param subProperties
   */
  private static void relationPropertyCreateOrUpdate(
      List<PropertyModel> properties,
      Relation relation,
      Map<String, PropertyModel> oldsubPropMap,
      List<String> deleteProps,
      List<SubProperty> subProperties) {
    for (PropertyModel newSubProp : properties) {
      BasicTypeEnum propertyType = getPropertyType(newSubProp.getType());
      SubProperty subProperty =
          new SubProperty(
              new BasicInfo<>(
                  new PredicateIdentifier(newSubProp.getName()),
                  newSubProp.getNameZh(),
                  StringUtils.EMPTY),
              relation.toRef(),
              new SPGTypeRef(
                  new BasicInfo<>(
                      new SPGTypeIdentifier(null, propertyType.getCode()),
                      propertyType.getDesc(),
                      propertyType.getDesc()),
                  SPGTypeEnum.BASIC_TYPE),
              new PropertyAdvancedConfig());
      PropertyModel propertyModel = oldsubPropMap.get(newSubProp.getName());
      if (propertyModel == null) {
        subProperty.setAlterOperation(AlterOperationEnum.CREATE);
      } else {
        deleteProps.remove(newSubProp.getName());
        subProperty.setOntologyId(propertyModel.getOntologyId());
        subProperty.setAlterOperation(AlterOperationEnum.UPDATE);
      }
      subProperties.add(subProperty);
    }
  }

  /**
   * Node add or delete
   *
   * @param namespace
   * @param nodes
   * @param advancedType
   * @param name2Type
   * @param alterOperation
   */
  private static void convertToAddOrDelete(
      String namespace,
      List<NodeTypeModel> nodes,
      List<BaseAdvancedType> advancedType,
      Map<String, String> name2Type,
      AlterOperationEnum alterOperation) {
    if (CollectionUtils.isEmpty(nodes)) {
      return;
    }
    if (name2Type.isEmpty()) {
      name2Type =
          nodes.stream().collect(Collectors.toMap(NodeTypeModel::getName, NodeTypeModel::getType));
    }
    for (NodeTypeModel oldType : nodes) {
      SPGTypeIdentifier spgTypeIdentifier = new SPGTypeIdentifier(namespace, oldType.getName());
      BasicInfo basicInfo =
          new BasicInfo<>(spgTypeIdentifier, oldType.getNameZh(), oldType.getDesc());
      ParentTypeInfo parentTypeInfo = ParentTypeInfo.THING;
      if (StringUtils.isNotBlank(oldType.getParentName())) {
        parentTypeInfo =
            new ParentTypeInfo(null, null, SPGTypeIdentifier.parse(oldType.getParentName()), null);
      }
      SPGTypeAdvancedConfig spgTypeAdvancedConfig = new SPGTypeAdvancedConfig();
      List<PropertyModel> props =
          CollectionUtils.isEmpty(oldType.getProperties())
              ? Lists.newArrayList()
              : oldType.getProperties();
      List<Property> propertyList = Lists.newArrayList();
      BaseAdvancedType type;
      AdvancedTypeEnum advancedTypeEnum = AdvancedTypeEnum.valueOf(oldType.getType());
      switch (advancedTypeEnum) {
        case ENTITY_TYPE:
          type =
              new EntityType(basicInfo, parentTypeInfo, propertyList, null, spgTypeAdvancedConfig);
          break;
        case EVENT_TYPE:
          type =
              new EventType(basicInfo, parentTypeInfo, propertyList, null, spgTypeAdvancedConfig);
          break;
        case INDEX_TYPE:
          type =
              new IndexType(basicInfo, parentTypeInfo, propertyList, null, spgTypeAdvancedConfig);
          break;
        case CONCEPT_TYPE:
          String hypernymPredicate =
              StringUtils.isBlank(oldType.getHypernymPredicate())
                  ? SystemPredicateEnum.IS_A.getName()
                  : oldType.getHypernymPredicate();
          type =
              new ConceptType(
                  basicInfo,
                  parentTypeInfo,
                  propertyList,
                  null,
                  spgTypeAdvancedConfig,
                  new ConceptLayerConfig(hypernymPredicate, null),
                  null,
                  null);
          break;
        default:
          throw new RuntimeException("unsupported advanced type: " + oldType.getType());
      }
      type.setOntologyId(oldType.getOntologyId());
      type.setExtInfo(new SchemaExtInfo());
      // Meta-concepts are currently not supported for directly pulling edges or creating
      // attributes; proceed directly to skipping.
      if (type instanceof ConceptType) {
        type.setAlterOperation(alterOperation);
        advancedType.add(type);
        continue;
      }
      for (PropertyModel prop : props) {
        SPGTypeRef propRef = getTargetType(prop.getType(), namespace, name2Type);
        boolean isInherited = isInherited(prop);
        Property property =
            new Property(
                new BasicInfo<>(
                    new PredicateIdentifier(prop.getName()), prop.getNameZh(), StringUtils.EMPTY),
                type.toRef(),
                propRef,
                isInherited,
                new PropertyAdvancedConfig().setIndexType(getIndexType(prop.getIndex())));
        property.setOntologyId(prop.getOntologyId());
        property.getAdvancedConfig().setLogicalRule(setLogicalRule(null, prop.getRule()));
        property.getAdvancedConfig().setConstraint(prop.getConstraint());
        property.setExtInfo(new SchemaExtInfo());
        buildEventProperty(type, property);
        if (!isInherited) {
          property.setAlterOperation(alterOperation);
        }
        propertyList.add(property);
      }
      if (CollectionUtils.isNotEmpty(oldType.getRelations())) {
        List<Relation> relations = Lists.newArrayList();
        List<EdgeTypeModel> relationModels = oldType.getRelations();
        for (EdgeTypeModel relationModel : relationModels) {
          PropertyAdvancedConfig propertyAdvancedConfig = new PropertyAdvancedConfig();
          boolean isInherited =
              relationModel.getInherited() != null && relationModel.getInherited();
          SPGTypeRef propRef = getTargetType(relationModel.getTargetType(), namespace, name2Type);
          Relation relation =
              new Relation(
                  new BasicInfo<>(
                      new PredicateIdentifier(relationModel.getName()),
                      relationModel.getNameZh(),
                      relationModel.getDesc()),
                  type.toRef(),
                  propRef,
                  isInherited,
                  propertyAdvancedConfig,
                  false);
          relation
              .getAdvancedConfig()
              .setLogicalRule(setLogicalRule(null, relationModel.getRule()));
          updateRelationProperty(relationModel, relationModel.getProperties(), relation);
          relation.setOntologyId(relationModel.getOntologyId());
          relation.setExtInfo(new SchemaExtInfo());
          if (!isInherited) {
            relation.setAlterOperation(alterOperation);
          }
          relations.add(relation);
        }
        type.setRelations(relations);
      }
      type.setAlterOperation(alterOperation);
      advancedType.add(type);
    }
  }

  private static void buildEventProperty(BaseAdvancedType advancedType, Property property) {
    if (advancedType instanceof EventType) {
      String name = property.getName();
      if (PropertyGroupEnum.SUBJECT.getNameEn().equalsIgnoreCase(name)) {
        property.getAdvancedConfig().setPropertyGroup(PropertyGroupEnum.SUBJECT);
      }
      if (PropertyGroupEnum.OBJECT.getNameEn().equalsIgnoreCase(name)) {
        property.getAdvancedConfig().setPropertyGroup(PropertyGroupEnum.OBJECT);
      }
    }
  }

  private static SPGTypeRef getTargetType(
      String targetType, String namespace, Map<String, String> typeMap) {
    SPGTypeRef spgTypeRef;
    if (BasicTypeEnum.isBasicType(targetType)) {
      BasicTypeEnum propertyType = getPropertyType(targetType);
      spgTypeRef =
          new SPGTypeRef(
              new BasicInfo<>(
                  new SPGTypeIdentifier(null, propertyType.getCode()),
                  propertyType.getDesc(),
                  propertyType.getDesc()),
              SPGTypeEnum.BASIC_TYPE);
    } else if (targetType.startsWith("STD.")) {
      spgTypeRef =
          new SPGTypeRef(
              new BasicInfo<>(SPGTypeIdentifier.parse(targetType)), SPGTypeEnum.STANDARD_TYPE);
    } else {
      SPGTypeIdentifier propSpgTypeIdentifier = new SPGTypeIdentifier(namespace, targetType);
      String s = typeMap.get(targetType);
      if (StringUtils.isBlank(s)) {
        throw new RuntimeException("can not find type: " + targetType);
      }
      spgTypeRef = new SPGTypeRef(new BasicInfo<>(propSpgTypeIdentifier), SPGTypeEnum.toEnum(s));
    }
    return spgTypeRef;
  }

  private static LogicalRule setLogicalRule(LogicalRule oldRule, LogicalRule newRule) {
    if (oldRule == null && newRule == null) {
      return null;
    }
    if (oldRule != null && newRule == null) {
      return null;
    }
    if (oldRule == null && newRule != null) {
      return newRule;
    }
    if (oldRule != null && newRule != null) {
      newRule.setCode(oldRule.getCode());
      return newRule;
    }
    return null;
  }

  /**
   * get index type by index type string
   *
   * @param index
   * @return
   */
  private static IndexTypeEnum getIndexType(String index) {
    IndexTypeEnum indexTypeEnum = null;
    if (StringUtils.isNotBlank(index)) {
      indexTypeEnum = IndexTypeEnum.valueOf(index.trim());
    }
    return indexTypeEnum;
  }

  /**
   * Determine if inheriting attributes.
   *
   * @param propertyModel
   * @return
   */
  private static boolean isInherited(PropertyModel propertyModel) {
    return propertyModel.getInherited() != null && propertyModel.getInherited();
  }

  private static BasicTypeEnum getPropertyType(String type) {
    if (StringUtils.isBlank(type)) {
      return BasicTypeEnum.TEXT;
    }
    return BasicTypeEnum.getByCode(type.trim());
  }

  @Getter
  public class SchemaChangeDTO {
    private List<BaseAdvancedType> addTypes;
    private List<BaseAdvancedType> updateTypes;
    private List<BaseAdvancedType> deleteTypes;

    public SchemaChangeDTO(
        List<BaseAdvancedType> addTypes,
        List<BaseAdvancedType> updateTypes,
        List<BaseAdvancedType> deleteTypes) {
      this.addTypes = addTypes == null ? new ArrayList<>() : addTypes;
      this.updateTypes = updateTypes == null ? new ArrayList<>() : updateTypes;
      this.deleteTypes = deleteTypes == null ? new ArrayList<>() : deleteTypes;
    }
  }
}
