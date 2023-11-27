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

package com.antgroup.openspg.server.schema.core.service.alter.check;

import com.antgroup.openspg.server.common.model.base.BaseToString;
import com.antgroup.openspg.server.core.schema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.server.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.core.schema.model.predicate.Property;
import com.antgroup.openspg.server.core.schema.model.predicate.Relation;
import com.antgroup.openspg.server.core.schema.model.type.BaseSPGType;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

/**
 * A container that holds three map, a map contains all spg types, a map contains all property
 * types, and a map contains all relation types. it's easy to find a spg type, a property type, or a
 * relation type through the container.
 */
public class SchemaMap extends BaseToString {

  private static final long serialVersionUID = -3371743831935454756L;

  /** A map contains spg types, the key is type name, and the value is type object. */
  private final Map<SPGTypeIdentifier, BaseSPGType> spgTypeMap = new HashMap<>();

  /** A map contains all properties, the key is property name, and the value is property object. */
  private final Map<SPGTripleIdentifier, Property> propertyMap = new HashMap<>();

  /** A map contains all relations, the key is relation name, and the value is relation object. */
  private final Map<SPGTripleIdentifier, Relation> relationMap = new HashMap<>();

  /**
   * Get the map of spg type.
   *
   * @return the map contains all spg types.
   */
  public Map<SPGTypeIdentifier, BaseSPGType> getSpgTypeMap() {
    return spgTypeMap;
  }

  /**
   * Get the map of property types.
   *
   * @return the map contains all property types.
   */
  public Map<SPGTripleIdentifier, Property> getPropertyMap() {
    return propertyMap;
  }

  /**
   * Get the map of relation types.
   *
   * @return the map contains all relation types.
   */
  public Map<SPGTripleIdentifier, Relation> getRelationMap() {
    return relationMap;
  }

  /**
   * Add the spg type into map.
   *
   * @param baseSpgType the spg type to add
   */
  public void addSpgType(BaseSPGType baseSpgType) {
    if (null == baseSpgType
        || baseSpgType.getBasicInfo() == null
        || baseSpgType.getBaseSpgIdentifier() == null) {
      throw new IllegalArgumentException("exist blank name type");
    }

    if (spgTypeMap.containsKey(baseSpgType.getBaseSpgIdentifier())) {
      throw new IllegalArgumentException(
          "exist same name type: " + baseSpgType.getBaseSpgIdentifier());
    }
    spgTypeMap.put(baseSpgType.getBaseSpgIdentifier(), baseSpgType);

    if (CollectionUtils.isNotEmpty(baseSpgType.getProperties())) {
      for (Property property : baseSpgType.getProperties()) {
        SPGTripleIdentifier tripleIdentifier = property.getSpgTripleName();
        if (propertyMap.containsKey(tripleIdentifier)) {
          throw new IllegalArgumentException("exist same name property: " + tripleIdentifier);
        }
        propertyMap.put(tripleIdentifier, property);
      }
    }
    if (CollectionUtils.isNotEmpty(baseSpgType.getRelations())) {
      for (Relation relation : baseSpgType.getRelations()) {
        SPGTripleIdentifier tripleIdentifier = relation.getSpgTripleName();
        if (relationMap.containsKey(tripleIdentifier)) {
          throw new IllegalArgumentException("exist same name relation: " + tripleIdentifier);
        }
        relationMap.put(relation.getSpgTripleName(), relation);
      }
    }
  }
}
