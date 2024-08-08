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

package com.antgroup.openspg.core.schema.model.semantic;

/** Enumeration of ontology types */
public enum SPGOntologyEnum {

  /** SPG type, such as EntityType, ConceptType, EventType etc. */
  TYPE,

  /** Property in the type. */
  PROPERTY,

  /** Relation in the type. */
  RELATION,

  /** SubProperty in the property or relation. */
  SUB_PROPERTY,

  /** Concept instance. */
  CONCEPT,

  /** Reasoning concept instance. */
  REASONING_CONCEPT;

  public static SPGOntologyEnum toEnum(String val) {
    for (SPGOntologyEnum resourceTypeEnum : SPGOntologyEnum.values()) {
      if (resourceTypeEnum.name().equalsIgnoreCase(val)) {
        return resourceTypeEnum;
      }
    }

    throw new IllegalArgumentException("unknown type: " + val);
  }
}
