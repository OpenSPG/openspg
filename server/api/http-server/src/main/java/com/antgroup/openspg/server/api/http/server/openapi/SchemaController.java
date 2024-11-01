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

package com.antgroup.openspg.server.api.http.server.openapi;

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.SchemaExtInfo;
import com.antgroup.openspg.core.schema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.schema.model.alter.SchemaDraft;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.IndexTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.predicate.SubProperty;
import com.antgroup.openspg.core.schema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.EntityType;
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeAdvancedConfig;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.server.api.facade.dto.schema.request.*;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.biz.schema.SchemaManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/v1/schema")
public class SchemaController extends BaseController {

  @Autowired private SchemaManager schemaManager;

  public static SchemaDraft getDefaultSchemaDraft(String namespace) {
    SchemaDraft sd = new SchemaDraft();
    List<BaseAdvancedType> alterSpgTypes = getDefaultSchema(namespace);
    sd.setAlterSpgTypes(alterSpgTypes);
    return sd;
  }

  public static List<BaseAdvancedType> getDefaultSchema(String namespace) {
    List<BaseAdvancedType> schemaTypes = Lists.newArrayList();

    Map<String, IndexTypeEnum> chunkProperties = Maps.newHashMap();
    chunkProperties.put("content", IndexTypeEnum.TEXT_AND_VECTOR);

    Map<String, IndexTypeEnum> properties = Maps.newHashMap();
    properties.put("desc", IndexTypeEnum.TEXT_AND_VECTOR);
    properties.put("semanticType", IndexTypeEnum.TEXT);

    schemaTypes.add(getBaseSPGType(namespace, "Chunk", "文本块", chunkProperties));
    schemaTypes.add(getBaseSPGType(namespace, "ArtificialObject", "人造物体", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Astronomy", "天文学", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Building", "建筑", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Creature", "生物", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Concept", "概念", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Date", "日期", properties));
    schemaTypes.add(getBaseSPGType(namespace, "GeographicLocation", "地理位置", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Keyword", "关键词", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Medicine", "药物", properties));
    schemaTypes.add(getBaseSPGType(namespace, "NaturalScience", "自然科学", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Organization", "组织机构", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Person", "人物", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Transport", "运输", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Works", "作品", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Event", "事件", properties));
    schemaTypes.add(getBaseSPGType(namespace, "Others", "其他", properties));
    schemaTypes.add(getBaseSPGType(namespace, "SemanticConcept", "语义概念", properties));
    return schemaTypes;
  }

  public static EntityType getBaseSPGType(
      String namespace, String label, String nameZh, Map<String, IndexTypeEnum> properties) {
    List<Property> propertyList = Lists.newArrayList();
    for (String pro : properties.keySet()) {

      Property property =
          new Property(
              new BasicInfo<>(new PredicateIdentifier(pro), pro, StringUtils.EMPTY),
              null,
              new SPGTypeRef(
                  new BasicInfo<>(new SPGTypeIdentifier(null, "Text"), "文本", "文本"),
                  SPGTypeEnum.BASIC_TYPE),
              false,
              new PropertyAdvancedConfig().setIndexType(properties.get(pro)));
      property.setAlterOperation(AlterOperationEnum.CREATE);
      propertyList.add(property);
    }

    EntityType entityType =
        new EntityType(
            new BasicInfo(new SPGTypeIdentifier(namespace, label), nameZh, StringUtils.EMPTY),
            ParentTypeInfo.THING,
            propertyList,
            null,
            null);
    entityType.setAlterOperation(AlterOperationEnum.CREATE);
    return entityType;
  }

  @RequestMapping(value = "/alterSchema", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Object> alterSchema(@RequestBody SchemaAlterRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("schemaDraft", request.getSchemaDraft());
          }

          @Override
          public Boolean action() {
            if (request.getSchemaDraft() == null
                || CollectionUtils.isEmpty(request.getSchemaDraft().getAlterSpgTypes())) {
              throw new IllegalArgumentException("schema draft is empty");
            }

            List<BaseAdvancedType> alterSpgTypes = request.getSchemaDraft().getAlterSpgTypes();
            for (BaseAdvancedType advancedType : alterSpgTypes) {
              if (CollectionUtils.isNotEmpty(advancedType.getProperties())) {
                for (Property property : advancedType.getProperties()) {
                  property.setSubjectTypeRef(advancedType.toRef());

                  if (CollectionUtils.isNotEmpty(property.getSubProperties())) {
                    for (SubProperty subProperty : property.getSubProperties()) {
                      subProperty.setSubjectTypeRef(property.toRef());
                    }
                  }
                  if (CollectionUtils.isNotEmpty(property.getSemantics())) {
                    for (PredicateSemantic semantic : property.getSemantics()) {
                      semantic.setSubjectTypeRef(property.toRef());
                    }
                  }
                  if (property.getExtInfo() == null) {
                    property.setExtInfo(new SchemaExtInfo());
                  }
                }
              }

              if (CollectionUtils.isNotEmpty(advancedType.getRelations())) {
                for (Relation relation : advancedType.getRelations()) {
                  relation.setSubjectTypeRef(advancedType.toRef());

                  if (CollectionUtils.isNotEmpty(relation.getSubProperties())) {
                    for (SubProperty subProperty :
                        relation.getAdvancedConfig().getSubProperties()) {
                      subProperty.setSubjectTypeRef(relation.toRef());
                    }
                  }
                  if (CollectionUtils.isNotEmpty(relation.getSemantics())) {
                    for (PredicateSemantic semantic : relation.getSemantics()) {
                      semantic.setSubjectTypeRef(relation.toRef());
                    }
                  }
                  if (relation.getExtInfo() == null) {
                    relation.setExtInfo(new SchemaExtInfo());
                  }
                }
              }

              if (advancedType.getParentTypeInfo() == null) {
                advancedType.setParentTypeInfo(ParentTypeInfo.THING);
              }
              if (advancedType.getAdvancedConfig() == null) {
                advancedType.setAdvancedConfig(new SPGTypeAdvancedConfig());
              }
              if (advancedType.getExtInfo() == null) {
                advancedType.setExtInfo(new SchemaExtInfo());
              }
            }
            schemaManager.alterSchema(request);
            return true;
          }
        });
  }

  @RequestMapping(value = "/queryProjectSchema", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> queryProjectSchema(ProjectSchemaRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ProjectSchema>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
          }

          @Override
          public ProjectSchema action() {
            return schemaManager.getProjectSchema(request.getProjectId());
          }
        });
  }

  @RequestMapping(value = "/querySpgType", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> querySpgType(SPGTypeRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<BaseSPGType>() {
          @Override
          public void check() {}

          @Override
          public BaseSPGType action() {
            return schemaManager.getSpgType(request.getName());
          }
        });
  }

  @RequestMapping(value = "/queryRelation", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> queryRelation(RelationRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Relation>() {
          @Override
          public void check() {}

          @Override
          public Relation action() {
            BaseSPGType spgType = schemaManager.getSpgType(request.getsName());
            if (null == spgType) {
              return null;
            }

            for (Relation relation : spgType.getRelations()) {
              if (relation.getName().equals(request.getRelation())
                  && relation.getObjectTypeRef().getName().equals(request.getoName())) {
                return relation;
              }
            }
            return null;
          }
        });
  }

  @RequestMapping(value = "/queryBuiltInProperty", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Object> queryBuiltInProperty(BuiltInPropertyRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<Property>>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("spgTypeEnum", request.getSpgTypeEnum());
          }

          @Override
          public List<Property> action() {
            return schemaManager.getBuiltInProperty(request.getSpgTypeEnum());
          }
        });
  }
}
