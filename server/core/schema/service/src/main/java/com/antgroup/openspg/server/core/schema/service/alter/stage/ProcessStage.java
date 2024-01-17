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

import com.antgroup.openspg.core.schema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.server.core.schema.service.alter.model.SchemaAlterContext;
import com.antgroup.openspg.server.core.schema.service.alter.stage.handler.OntologyIdHandler;
import com.antgroup.openspg.server.core.schema.service.type.SPGTypeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Persist schema metadata, save the new schema to local database, if the project used for online
 * querying, the new schema is also saved to graph storage.
 */
@Slf4j
public class ProcessStage extends BaseAlterStage {

  @Autowired private SPGTypeService spgTypeService;
  @Autowired private OntologyIdHandler ontologyIdHandler;

  public ProcessStage() {
    super("schema-persist-stage");
  }

  @Override
  public void execute(SchemaAlterContext context) {
    this.setIdAndVersion(context);
    this.alterMeta(context);
  }

  private void setIdAndVersion(SchemaAlterContext context) {
    ontologyIdHandler.handle(context);
  }

  private void alterMeta(SchemaAlterContext context) {
    List<BaseAdvancedType> deleteTypes =
        context.getAlterTypeByAlterOperation(AlterOperationEnum.DELETE);
    deleteTypes.forEach(e -> spgTypeService.delete(e));

    List<BaseAdvancedType> updateTypes =
        context.getAlterTypeByAlterOperation(AlterOperationEnum.UPDATE);
    updateTypes.forEach(e -> spgTypeService.update(e));

    List<BaseAdvancedType> createTypes =
        context.getAlterTypeByAlterOperation(AlterOperationEnum.CREATE);
    createTypes.forEach(e -> spgTypeService.create(e));

    log.info("all alter operation of ontology is saved to db");
  }
}
