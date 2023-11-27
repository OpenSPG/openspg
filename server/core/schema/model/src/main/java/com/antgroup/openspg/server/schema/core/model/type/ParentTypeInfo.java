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

package com.antgroup.openspg.server.schema.core.model.type;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.core.spgschema.model.SchemaConstants;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import java.util.List;

/**
 * Parent class information.<br>
 * <br>
 *
 * <p>Similar to object-oriented programming languages, there is an inheritance relationship between
 * entities, and each entity type has a parent class. Otherwise, it defaults to inheriting from
 * "Thing", For example, the parent class of humans is animals, the parent class of animals is
 * organisms, and the parent class of organisms is Thing.<br>
 * <br>
 * Under the SPG knowledge representation framework, subclasses will default to inheriting all
 * attributes and semantic relationships of the parent class. <br>
 * For example, in the business of Ant Group, there are KouBeiShop and ElemeShop, and the properties
 * of the two stores are basically the same, represented by entity inheritance:
 *
 * <ul>
 *   <li>(KouBeiShop subClassOf Shop)
 *   <li>(ElemeShop subClassOf Shop)
 * </ul>
 */
public class ParentTypeInfo extends BaseValObj {

  private static final long serialVersionUID = -2227417819589738306L;

  public static final ParentTypeInfo THING =
      new ParentTypeInfo(
          null, null, new SPGTypeIdentifier(null, SchemaConstants.ROOT_TYPE_UNIQUE_NAME), null);

  /** The unique id of the entity type */
  private final Long uniqueId;

  /** The unique id of the parent entity */
  private final Long parentUniqueId;

  /** The unique name of the parent entity */
  private final SPGTypeIdentifier parentTypeIdentifier;

  /** The inherited path, include unique ids from the "Thing" entity to the current entity */
  private final List<Long> inheritPath;

  public ParentTypeInfo(
      Long uniqueId,
      Long parentUniqueId,
      SPGTypeIdentifier parentTypeIdentifier,
      List<Long> inheritPath) {
    this.uniqueId = uniqueId;
    this.parentUniqueId = parentUniqueId;
    this.parentTypeIdentifier = parentTypeIdentifier;
    this.inheritPath = inheritPath;
  }

  public Long getUniqueId() {
    return uniqueId;
  }

  public Long getParentUniqueId() {
    return parentUniqueId;
  }

  public SPGTypeIdentifier getParentTypeIdentifier() {
    return parentTypeIdentifier;
  }

  public List<Long> getInheritPath() {
    return inheritPath;
  }

  public ParentTypeInfo withNewParentInfo(
      Long uniqueId, Long parentUniqueId, List<Long> inheritPath) {
    return new ParentTypeInfo(uniqueId, parentUniqueId, parentTypeIdentifier, inheritPath);
  }
}
