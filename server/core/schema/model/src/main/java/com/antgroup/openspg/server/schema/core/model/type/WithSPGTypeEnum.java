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

/** An interface provides some methods to get and check type enumeration. */
public interface WithSPGTypeEnum {

  /**
   * Get enumeration of spg type.
   *
   * @return enumeration
   */
  SPGTypeEnum getSpgTypeEnum();

  /**
   * If the type is an instance of AdvancedType.
   *
   * @return true/false
   */
  default boolean isAdvancedType() {
    return !isBasicType();
  }

  /**
   * If the type is an instance of BasicType.
   *
   * @return true/false
   */
  default boolean isBasicType() {
    return SPGTypeEnum.BASIC_TYPE.equals(getSpgTypeEnum());
  }

  /**
   * If the type is an instance of EntityType.
   *
   * @return true/false
   */
  default boolean isEntityType() {
    return SPGTypeEnum.ENTITY_TYPE.equals(getSpgTypeEnum());
  }

  /**
   * If the type is an instance of ConceptType.
   *
   * @return true/false
   */
  default boolean isConceptType() {
    return SPGTypeEnum.CONCEPT_TYPE.equals(getSpgTypeEnum());
  }

  /**
   * If the type is an instance of EventType.
   *
   * @return true/false
   */
  default boolean isEventType() {
    return SPGTypeEnum.EVENT_TYPE.equals(getSpgTypeEnum());
  }

  /**
   * If the type is an instance of StandardType.
   *
   * @return true/false
   */
  default boolean isStandardType() {
    return SPGTypeEnum.STANDARD_TYPE.equals(getSpgTypeEnum());
  }
}
