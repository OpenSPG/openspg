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

import com.antgroup.openspg.schema.model.constraint.Constraint;
import com.antgroup.openspg.schema.model.constraint.ConstraintTypeEnum;

public class CommonChecker {

  /**
   * check if contains forbidden constraint.
   *
   * @param constraint constraint config
   * @param constraintType forbidden constraint type
   */
  public static void containForbiddenConstraintType(
      Constraint constraint, ConstraintTypeEnum constraintType) {
    if (null == constraint) {
      return;
    }

    if (constraint.contains(constraintType)) {
      throw new IllegalArgumentException("number property can not have multi value constraint");
    }
  }
}
