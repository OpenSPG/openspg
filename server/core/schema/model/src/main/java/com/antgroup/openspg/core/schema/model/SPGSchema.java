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

package com.antgroup.openspg.core.schema.model;

import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.List;
import java.util.Set;

/** Details of SPG schema, contains all SPG types that altered. */
public class SPGSchema extends BaseValObj {

  private static final long serialVersionUID = 9081538161078193619L;

  /** The list of SPG types that altered. */
  private final List<BaseSPGType> spgTypes;

  /** The set of standard type's name which is spreadable. */
  private final Set<SPGTypeIdentifier> spreadStdTypeNames;

  public SPGSchema(List<BaseSPGType> spgTypes, Set<SPGTypeIdentifier> spreadStdTypeNames) {
    this.spgTypes = spgTypes;
    this.spreadStdTypeNames = spreadStdTypeNames;
  }

  public List<BaseSPGType> getSpgTypes() {
    return spgTypes;
  }

  public Set<SPGTypeIdentifier> getSpreadStdTypeNames() {
    return spreadStdTypeNames;
  }
}
