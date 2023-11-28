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

package com.antgroup.openspg.schema.model.alter;

import com.antgroup.openspg.common.model.base.BaseToString;
import com.antgroup.openspg.schema.model.type.BaseAdvancedType;
import java.util.List;

/**
 * The schema alternation of project ontologies. Every schema change must be based on the latest
 * online version of the schema. The schema draft contains the complete structure of the altered
 * ontology, and will be submitted to the server to perform the alter operation.
 */
public class SchemaDraft extends BaseToString {

  private static final long serialVersionUID = -5014220341785954801L;

  /** The alter content of entity type. */
  private List<BaseAdvancedType> alterSpgTypes;

  public List<BaseAdvancedType> getAlterSpgTypes() {
    return alterSpgTypes;
  }

  public void setAlterSpgTypes(List<BaseAdvancedType> alterSpgTypes) {
    this.alterSpgTypes = alterSpgTypes;
  }
}
