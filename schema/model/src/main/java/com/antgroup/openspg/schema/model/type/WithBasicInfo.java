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

package com.antgroup.openspg.schema.model.type;

import com.antgroup.openspg.schema.model.BasicInfo;
import com.antgroup.openspg.schema.model.identifier.BaseSPGIdentifier;

/** An interface provides some methods to get basic information of type. */
public interface WithBasicInfo<N extends BaseSPGIdentifier> {

  /**
   * Get basic information of onotlogy.
   *
   * @return basic info
   */
  BasicInfo<N> getBasicInfo();

  /**
   * Get SPG identifier of ontology
   *
   * @return identifier
   */
  default N getBaseSpgIdentifier() {
    return getBasicInfo().getName();
  }

  /**
   * Get name of ontology.
   *
   * @return name string
   */
  default String getName() {
    return getBaseSpgIdentifier().toString();
  }
}
