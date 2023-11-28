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

package com.antgroup.openspg.core.schema.model.type;

import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;

import java.util.List;

/** An interface provides some methods to get parent type info. */
public interface WithParentTypeInfo {

  /**
   * Get parent type information
   *
   * @return info
   */
  ParentTypeInfo getParentTypeInfo();

  /**
   * Get numeric unique id of parent type
   *
   * @return id
   */
  default Long getParentUniqueId() {
    return getParentTypeInfo() == null ? null : getParentTypeInfo().getParentUniqueId();
  }

  /**
   * Get identifier of parent type.
   *
   * @return identifier
   */
  default SPGTypeIdentifier getParentTypeName() {
    return getParentTypeInfo() == null ? null : getParentTypeInfo().getParentTypeIdentifier();
  }

  /**
   * Get inherit parent of type
   *
   * @return list of id
   */
  default List<Long> getInheritPath() {
    return getParentTypeInfo() == null ? null : getParentTypeInfo().getInheritPath();
  }
}
