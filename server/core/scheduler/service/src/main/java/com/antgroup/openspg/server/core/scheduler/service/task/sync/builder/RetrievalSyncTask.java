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
package com.antgroup.openspg.server.core.scheduler.service.task.sync.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.alter.SchemaDraft;
import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.server.biz.schema.SchemaManager;
import com.antgroup.openspg.server.biz.schema.model.NodeTypeModel;
import com.antgroup.openspg.server.biz.schema.model.SchemaCompareUtil;
import com.antgroup.openspg.server.biz.schema.model.SchemaModel;
import com.antgroup.openspg.server.biz.schema.model.SchemaModelConvertor;
import com.antgroup.openspg.server.biz.schema.model.SchemaScriptTranslateUtil;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.model.retrieval.Retrieval;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.common.service.retrieval.RetrievalService;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.task.sync.SyncTaskExecuteTemplate;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("retrievalSyncTask")
public class RetrievalSyncTask extends SyncTaskExecuteTemplate {

  private static final List<SPGTypeEnum> PAGE_DISPLAY_TYPE =
      Lists.newArrayList(
          SPGTypeEnum.ENTITY_TYPE,
          SPGTypeEnum.EVENT_TYPE,
          SPGTypeEnum.CONCEPT_TYPE,
          SPGTypeEnum.INDEX_TYPE);

  @Autowired private BuilderJobService builderJobService;

  @Autowired private RetrievalService retrievalService;

  @Autowired private ProjectService projectService;

  @Autowired private SchemaManager schemaManager;

  @Override
  public SchedulerEnum.TaskStatus submit(TaskExecuteContext context) {

    SchedulerJob job = context.getJob();
    BuilderJob builderJob = builderJobService.getById(Long.valueOf(job.getInvokerId()));
    String retrievals = builderJob.getRetrievals();
    if (StringUtils.isBlank(retrievals)) {
      context.addTraceLog("index not set");
      return SchedulerEnum.TaskStatus.FINISH;
    }
    context.addTraceLog("update index schema index_ids:%s", retrievals);
    List<Long> retrievalList = JSON.parseObject(retrievals, new TypeReference<List<Long>>() {});
    Project project = projectService.queryById(job.getProjectId());
    if (project == null) {
      context.addTraceLog("project not exist");
      return SchedulerEnum.TaskStatus.FINISH;
    }
    for (Long id : retrievalList) {
      Retrieval retrieval = retrievalService.getById(id);
      context.addTraceLog("update index(%s) schema", retrieval.getName());
      String schemaDesc = retrieval.getSchemaDesc();
      if (StringUtils.isBlank(schemaDesc)) {
        continue;
      }
      SchemaDraft schemaDraft = getSpgSchema(project, schemaDesc);
      SchemaAlterRequest request = new SchemaAlterRequest();
      request.setProjectId(project.getId());
      request.setSchemaDraft(schemaDraft);
      schemaManager.alterSchema(request);
      context.addTraceLog("update index(%s) schema succeed", retrieval.getName());
    }
    return SchedulerEnum.TaskStatus.FINISH;
  }

  private SchemaDraft getSpgSchema(Project project, String schema) {
    SchemaModel schemaModel =
        SchemaScriptTranslateUtil.translateScript(
            "namespace " + project.getNamespace() + "\n\n" + schema);

    ProjectSchema projectSchema = schemaManager.getProjectSchema(project.getId());
    List<NodeTypeModel> oldNodeTypeModels = Lists.newArrayList();
    for (BaseSPGType spgType : projectSchema.getSpgTypes()) {
      if (PAGE_DISPLAY_TYPE.contains(spgType.getSpgTypeEnum())) {
        NodeTypeModel entityModel =
            SchemaModelConvertor.convert2NodeTypeModel(
                project.getNamespace(), (BaseAdvancedType) spgType);
        oldNodeTypeModels.add(entityModel);
      }
    }
    List<NodeTypeModel> nodeTypeModels = schemaModel.getNodeTypeModels();
    Set<String> nameSet =
        nodeTypeModels.stream().map(NodeTypeModel::getName).collect(Collectors.toSet());
    for (NodeTypeModel model : oldNodeTypeModels) {
      if (!nameSet.contains(model.getName())) {
        nodeTypeModels.add(model);
      }
    }
    SchemaCompareUtil schemaCompareUtil = new SchemaCompareUtil();
    SchemaCompareUtil.SchemaChangeDTO changeDTO =
        schemaCompareUtil.compare(schemaModel.getNamespace(), oldNodeTypeModels, nodeTypeModels);
    log.info(
        "RetrievalSyncTask changeDTO-ADD({})-DEL({})-UPDATE({})",
        changeDTO.getAddTypes().size(),
        changeDTO.getDeleteTypes().size(),
        changeDTO.getUpdateTypes().size());
    SchemaDraft schemaDraft = new SchemaDraft();
    List<BaseAdvancedType> newSchema = Lists.newArrayList();
    newSchema.addAll(changeDTO.getAddTypes());
    newSchema.addAll(changeDTO.getUpdateTypes());
    schemaDraft.setAlterSpgTypes(newSchema);
    return schemaDraft;
  }
}
