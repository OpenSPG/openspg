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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation;

import lombok.Getter;

@Getter
public class CreateIndexOperation extends BaseSchemaAtomicOperation {

  private final String propertyName;

  private final Boolean isUnique;

  private final Boolean isGlobal;

  public CreateIndexOperation(String propertyName, Boolean isUnique, Boolean isGlobal) {
    super(SchemaAtomicOperationEnum.CREATE_INDEX);
    this.propertyName = propertyName;
    this.isUnique = isUnique;
    this.isGlobal = isGlobal;
  }

  public CreateIndexOperation(String propertyName, Boolean isUnique) {
    this(propertyName, isUnique, null);
  }

  public CreateIndexOperation(String propertyName) {
    this(propertyName, null, null);
  }
}
