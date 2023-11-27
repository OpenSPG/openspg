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

package com.antgroup.openspg.server.schema.core.service.alter.stage;

import com.antgroup.openspg.core.spgschema.service.alter.model.SchemaAlterContext;

/**
 * An abstract class in the schema change phase, representing a certain processing in the schema
 * alter process. A {@link com.alipay.sofa.ark.spi.pipeline} consists of multiple stages.
 */
public abstract class BaseAlterStage {

  /** The stage name */
  private final String name;

  /**
   * Create a stage
   *
   * @param name stage name
   */
  public BaseAlterStage(String name) {
    this.name = name;
  }

  /**
   * start to execute the stage.
   *
   * @param context the context of schema altering
   */
  public abstract void execute(SchemaAlterContext context);

  /**
   * Get stage name
   *
   * @return stage name
   */
  public String getName() {
    return name;
  }
}
