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

package com.antgroup.openspg.server.core.schema.service.alter.stage;

import com.antgroup.openspg.common.util.constants.CommonConstant;
import com.antgroup.openspg.core.schema.model.SPGSchema;
import com.antgroup.openspg.core.schema.model.SPGSchemaAlterCmd;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.core.schema.service.alter.model.SchemaAlterContext;
import com.antgroup.openspg.server.core.schema.service.alter.sync.BaseSchemaSyncer;
import com.antgroup.openspg.server.core.schema.service.alter.sync.SchemaStorageEnum;
import com.antgroup.openspg.server.core.schema.service.alter.sync.SchemaSyncerFactory;
import com.antgroup.openspg.server.core.schema.service.type.SPGTypeService;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Post process after saving schema metadata to db. such as notify other system、save snapshot etc.
 */
@Slf4j
public class PostProcessStage extends BaseAlterStage {

  @Autowired private SchemaSyncerFactory schemaSyncerFactory;
  @Autowired private SPGTypeService spgTypeService;
  @Autowired private ProjectService projectManager;

  /** Target storage to sync schema */
  private static final List<SchemaStorageEnum> TARGET_SYNC_STORE =
      Lists.newArrayList(SchemaStorageEnum.GRAPH, SchemaStorageEnum.SEARCH_ENGINE);

  public PostProcessStage() {
    super("post-process-stage");
  }

  @Override
  public void execute(SchemaAlterContext context) {
    this.syncSchema(context);
  }

  private void syncSchema(SchemaAlterContext context) {
    Set<SPGTypeIdentifier> spreadStdTypeNames = spgTypeService.querySpreadStdTypeName();
    List<BaseSPGType> spgTypes =
        context.getAlterSchema().stream().map(e -> (BaseSPGType) e).collect(Collectors.toList());
    SPGSchemaAlterCmd schemaEditCmd =
        new SPGSchemaAlterCmd(new SPGSchema(spgTypes, spreadStdTypeNames));

    Long projectId = context.getProject().getId();
    String graphStoreEngineType = getGraphStoreEngineType(projectId);
    // TODO: 这里先跳过，kgfabric driver schema/索引同步打通后放开 @秉初 底层支持索引灵活变更的接口5月底给出，排期530开发
    if (CommonConstant.KGFABRIC_GRAPH_STORE.equalsIgnoreCase(graphStoreEngineType)) {
      return;
    }
    for (SchemaStorageEnum targetStorage : TARGET_SYNC_STORE) {
      BaseSchemaSyncer schemaSyncer = schemaSyncerFactory.getSchemaSyncer(targetStorage);
      if (schemaSyncer != null) {
        schemaSyncer.syncSchema(context.getProject().getId(), schemaEditCmd);
      }
    }
  }

  /**
   * TODO：获取存储引擎类型的临时方法，对接kgfabric后下掉，这里不感知引擎类型
   *
   * @param projectId
   * @return
   */
  private String getGraphStoreEngineType(Long projectId) {
    String graphStoreUrl = projectManager.getGraphStoreUrl(projectId);
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(graphStoreUrl).build();
    return uriComponents.getScheme();
  }
}
