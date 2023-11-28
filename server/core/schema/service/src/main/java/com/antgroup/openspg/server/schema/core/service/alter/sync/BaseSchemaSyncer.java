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

package com.antgroup.openspg.server.schema.core.service.alter.sync;

import com.antgroup.openspg.server.common.service.datasource.DataSourceService;
import com.antgroup.openspg.schema.model.SPGSchemaAlterCmd;
import org.springframework.beans.factory.annotation.Autowired;

/** The abstract class of schema writer. */
public abstract class BaseSchemaSyncer {

  @Autowired protected DataSourceService dataSourceService;

  /**
   * sync new schema of project into db.
   *
   * @param projectId the context of deploy pipeline
   * @param schemaEditCmd sync cmd
   */
  public abstract void syncSchema(Long projectId, SPGSchemaAlterCmd schemaEditCmd);
}
