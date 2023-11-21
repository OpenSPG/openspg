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

package com.antgroup.openspg.core.spgschema.service.alter;

import com.antgroup.openspg.core.spgschema.service.alter.model.SchemaAlterContext;
import com.antgroup.openspg.core.spgschema.service.alter.stage.BaseAlterStage;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Alter schema involve multiple operations, including steps such as building a new project schema,
 * persisting to database and graph storage, and cleaning up schema drafts. these processes are
 * abstracted to different stages. The pipeline connects the execution of stages to represent a
 * complete schema altering.
 */
@Slf4j
public class SchemaAlterPipeline {

  /** Every stage during pipeline running. */
  private List<BaseAlterStage> stages;

  /**
   * Start to run the pipeline, it will execute every stage defined in pipeline.
   *
   * @param context holds information that used during schema alter, such as project、draft etc.
   */
  public void run(SchemaAlterContext context) {
    for (BaseAlterStage stage : stages) {
      stage.execute(context);
      log.info("finish to execute stage: {}", stage.getName());
    }
  }

  public void setStages(List<BaseAlterStage> stages) {
    this.stages = stages;
  }
}
