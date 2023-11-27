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

package com.antgroup.openspg.server.schema.core.model;

import com.antgroup.openspg.common.model.base.BaseCmd;

/** Command to alter the SPG schema, including the set of SPG types to be altered. */
public class SPGSchemaAlterCmd extends BaseCmd {

  private static final long serialVersionUID = -4186195759403800416L;

  /** Details of SPG schema. */
  private final SPGSchema spgSchema;

  public SPGSchemaAlterCmd(SPGSchema spgSchema) {
    this.spgSchema = spgSchema;
  }

  public SPGSchema getSpgSchema() {
    return spgSchema;
  }
}
