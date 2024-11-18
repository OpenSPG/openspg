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

import com.antgroup.openspg.core.schema.model.alter.AlterOperationEnum;

/** An interface provides some methods to check and get altering operation. */
public interface WithAlterOperation {

  /**
   * Get alter operation.
   *
   * @return true/false
   */
  AlterOperationEnum getAlterOperation();

  /**
   * If the alter operation is creating
   *
   * @return true/false
   */
  default boolean isCreate() {
    return AlterOperationEnum.CREATE.equals(getAlterOperation());
  }

  /**
   * If the alter operation is updating.
   *
   * @return true/false
   */
  default boolean isUpdate() {
    return AlterOperationEnum.UPDATE.equals(getAlterOperation());
  }

  /**
   * If the alter operation is deleting.
   *
   * @return true/false
   */
  default boolean isDelete() {
    return AlterOperationEnum.DELETE.equals(getAlterOperation());
  }
}
