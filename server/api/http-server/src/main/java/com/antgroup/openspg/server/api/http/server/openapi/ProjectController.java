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
import com.antgroup.openspg.core.schema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.schema.model.alter.SchemaDraft;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.IndexTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.schema.model.type.EntityType;
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.common.model.project.Project;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/public/v1/project")
public class ProjectController extends BaseController {

  @Autowired private SchemaController schemaController;

  @Autowired private ProjectManager projectManager;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Object> create(@RequestBody ProjectCreateRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Project>() {
          @Override
          public void check() {}

          @Override
          public Project action() {
            Project project = projectManager.create(request);
            if (request.getAutoSchema() == null || Boolean.TRUE.equals(request.getAutoSchema())) {
              SchemaAlterRequest request = new SchemaAlterRequest();
              request.setProjectId(project.getId());
              request.setSchemaDraft(getDefaultSchemaDraft(project.getNamespace()));
              schemaController.alterSchema(request);
            }
            return project;
          }
        });
  }

  private SchemaDraft getDefaultSchemaDraft(String namespace) {
    SchemaDraft sd = new SchemaDraft();
    List<BaseAdvancedType> alterSpgTypes = getDefaultSchema(namespace);
    sd.setAlterSpgTypes(alterSpgTypes);
    return sd;
  }

  private List<BaseAdvancedType> getDefaultSchema(String namespace) {
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

  private EntityType getBaseSPGType(
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

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<Object> query(
      @RequestParam(required = false) Long tenantId,
      @RequestParam(required = false) Long projectId) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<Project>>() {
          @Override
          public void check() {}

          @Override
          public List<Project> action() {
            ProjectQueryRequest request = new ProjectQueryRequest();
            request.setTenantId(tenantId);
            request.setProjectId(projectId);
            return projectManager.query(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/update")
  public ResponseEntity<Object> update(@RequestBody ProjectCreateRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<Project>() {
          @Override
          public void check() {}

          @Override
          public Project action() {
            return projectManager.update(request);
          }
        });
  }
}
