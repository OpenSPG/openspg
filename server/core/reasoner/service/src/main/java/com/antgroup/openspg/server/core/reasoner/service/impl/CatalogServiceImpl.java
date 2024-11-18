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
package com.antgroup.openspg.server.core.reasoner.service.impl;

import com.antgroup.openspg.cloudext.interfaces.graphstore.BaseLPGGraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.EntityType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeAdvancedConfig;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.catalog.impl.OpenSPGCatalog;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.core.reasoner.service.CatalogService;
import com.antgroup.openspg.server.core.schema.service.type.SPGTypeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CatalogServiceImpl implements CatalogService {

  @Autowired private AppEnvConfig appEnvConfig;

  @Autowired private SPGTypeService spgTypeService;

  @Autowired private ProjectService projectService;

  @PostConstruct
  public void init() {}

  @Override
  public Catalog getCatalog(Long projectId, String graphStoreUrl) {
    return fetchCatalogByProjectId(projectId, graphStoreUrl);
  }

  protected Catalog fetchCatalogByProjectId(Long projectId, String graphStoreUrl) {
    long startTime = System.currentTimeMillis();
    Catalog catalog =
        new OpenSPGCatalog(
            projectId,
            new KgSchemaConnectionInfo(appEnvConfig.getSchemaUri(), ""),
            getSchemaInfo(projectId, graphStoreUrl));
    catalog.init();
    log.info("fetch catalog " + projectId + " cost=" + (System.currentTimeMillis() - startTime));
    return catalog;
  }

  private EntityType generateEntityTypeByName(String namespace, String name) {
    BasicInfo<SPGTypeIdentifier> basicInfo =
        new BasicInfo<>(new SPGTypeIdentifier(namespace, name));
    List<Property> properties =
        Lists.newArrayList(
            new Property(
                new BasicInfo<PredicateIdentifier>(
                    new PredicateIdentifier("description"), "描述", "描述"),
                new SPGTypeRef(basicInfo, SPGTypeEnum.ENTITY_TYPE),
                new SPGTypeRef(
                    new BasicInfo<SPGTypeIdentifier>(
                        new SPGTypeIdentifier(null, "Text"), "文本", "文本"),
                    SPGTypeEnum.BASIC_TYPE),
                false,
                new PropertyAdvancedConfig()),
            new Property(
                new BasicInfo<PredicateIdentifier>(new PredicateIdentifier("id"), "实体主键", "实体主键"),
                new SPGTypeRef(basicInfo, SPGTypeEnum.ENTITY_TYPE),
                new SPGTypeRef(
                    new BasicInfo<SPGTypeIdentifier>(
                        new SPGTypeIdentifier(null, "Text"), "文本", "文本"),
                    SPGTypeEnum.BASIC_TYPE),
                false,
                new PropertyAdvancedConfig()),
            new Property(
                new BasicInfo<PredicateIdentifier>(new PredicateIdentifier("name"), "名称", "名称"),
                new SPGTypeRef(basicInfo, SPGTypeEnum.ENTITY_TYPE),
                new SPGTypeRef(
                    new BasicInfo<SPGTypeIdentifier>(
                        new SPGTypeIdentifier(null, "Text"), "文本", "文本"),
                    SPGTypeEnum.BASIC_TYPE),
                false,
                new PropertyAdvancedConfig()),
            new Property(
                new BasicInfo<PredicateIdentifier>(new PredicateIdentifier("desc"), "描述", "描述"),
                new SPGTypeRef(basicInfo, SPGTypeEnum.ENTITY_TYPE),
                new SPGTypeRef(
                    new BasicInfo<SPGTypeIdentifier>(
                        new SPGTypeIdentifier(null, "Text"), "文本", "文本"),
                    SPGTypeEnum.BASIC_TYPE),
                false,
                new PropertyAdvancedConfig()),
            new Property(
                new BasicInfo<PredicateIdentifier>(
                    new PredicateIdentifier("semanticType"), "语义类型", "语义类型"),
                new SPGTypeRef(basicInfo, SPGTypeEnum.ENTITY_TYPE),
                new SPGTypeRef(
                    new BasicInfo<SPGTypeIdentifier>(
                        new SPGTypeIdentifier(null, "Text"), "文本", "文本"),
                    SPGTypeEnum.BASIC_TYPE),
                false,
                new PropertyAdvancedConfig()));
    List<Relation> relations = new ArrayList<>();

    SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig();

    return new EntityType(basicInfo, null, properties, relations, advancedConfig);
  }

  @Override
  public ProjectSchema getSchemaInfo(Long projectId, String graphStoreUrl) {
    Project project = projectService.queryById(projectId);
    if (project == null) {
      throw new IllegalArgumentException(projectId + " is not exists");
    }
    ProjectSchema projectSchema = spgTypeService.queryProjectSchema(projectId);
    BaseLPGGraphStoreClient lpgGraphStoreClient =
        (BaseLPGGraphStoreClient) GraphStoreClientDriverManager.getClient(graphStoreUrl);
    List<String> allLabelsInDataBase = lpgGraphStoreClient.queryAllVertexLabels();
    Set<String> notInSchemaManagerLabels = Sets.newHashSet("Entity");
    for (String label : allLabelsInDataBase) {
      BaseSPGType t = projectSchema.getByName(label);
      if (t != null) {
        continue;
      }
      notInSchemaManagerLabels.add(label);
    }

    // add Entity schema
    for (String label : notInSchemaManagerLabels) {
      SPGTypeIdentifier identifier = SPGTypeIdentifier.parse(label);
      EntityType entityType =
          generateEntityTypeByName(identifier.getNamespace(), identifier.getNameEn());
      projectSchema.getSpgTypes().add(entityType);
    }

    return projectSchema;
  }
}
