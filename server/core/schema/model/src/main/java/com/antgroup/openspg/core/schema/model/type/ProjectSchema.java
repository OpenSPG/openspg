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

package com.antgroup.openspg.core.schema.model.type;

import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.server.common.model.base.BaseToString;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

/** The schema details of the project, contains a list of SPG types. */
public class ProjectSchema extends BaseToString {

  private static final long serialVersionUID = 6290975442808465802L;

  /** List of SPG types that defined in the project. */
  @Getter private final List<BaseSPGType> spgTypes;

  /**
   * A map contains all the SPG types that defined in the project. the key is the name of the type,
   * and the value is the type.
   */
  private transient volatile Map<SPGTypeIdentifier, BaseSPGType> spgTypeMap;

  public ProjectSchema(List<BaseSPGType> spgTypes) {
    this.spgTypes = spgTypes;
  }

  /**
   * Get SPG type object by type name.
   *
   * @param name Name of the spg type
   * @return The SPG type object, it will return null if the name is not found.
   */
  public BaseSPGType getByName(SPGTypeIdentifier name) {
    if (spgTypeMap == null) {
      spgTypeMap =
          spgTypes.stream().collect(Collectors.toMap(WithBasicInfo::getBaseSpgIdentifier, x -> x));
    }
    return spgTypeMap.get(name);
  }

  public Relation getByName(RelationIdentifier identifier) {
    BaseSPGType spgType = getByName(identifier.getStart());
    if (spgType == null) {
      return null;
    }
    return spgType.getRelationByName(
        new SPGTripleIdentifier(
            identifier.getStart(), identifier.getPredicate(), identifier.getEnd()));
  }

  public boolean getSpreadable(SPGTypeIdentifier identifier) {
    BaseSPGType spgType = getByName(identifier);
    if (!(spgType instanceof StandardType)) {
      throw new IllegalArgumentException("illegal standardType=" + identifier);
    }
    return ((StandardType) spgType).getSpreadable();
  }

  /**
   * Get SPG type object by type name.
   *
   * @param name The unique name of the SPG type
   * @return the SPG type object
   */
  public BaseSPGType getByName(String name) {
    return getByName(SPGTypeIdentifier.parse(name));
  }

  /**
   * Get SPG type object by ref object.
   *
   * @param ref The reference of the SPG type
   * @return the SPG type object
   */
  public BaseSPGType getByRef(SPGTypeRef ref) {
    return getByName(ref.getBaseSpgIdentifier());
  }
}
