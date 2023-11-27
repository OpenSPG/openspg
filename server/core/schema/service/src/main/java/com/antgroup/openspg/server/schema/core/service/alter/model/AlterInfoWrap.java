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

package com.antgroup.openspg.server.schema.core.service.alter.model;

import com.antgroup.openspg.core.spgschema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Schema alter information wrapper, holding altered spg type, properties and relations. */
@Getter
@AllArgsConstructor
public class AlterInfoWrap {

  /** List of altered spg type, has sorted by inherit path. */
  private final List<BaseAdvancedType> sortedAlterTypes;

  /** Map contains altered spg type */
  private final Map<SPGTypeIdentifier, BaseSPGType> spgTypeMap;

  /** Map contains all properties in the spg type. */
  private final Map<SPGTripleIdentifier, Property> propertyMap;

  /** Map contains all relations in the spg type. */
  private final Map<SPGTripleIdentifier, Relation> relationMap;
}
