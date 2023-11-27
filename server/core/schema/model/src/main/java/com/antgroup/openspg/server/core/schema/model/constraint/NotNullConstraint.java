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

package com.antgroup.openspg.server.core.schema.model.constraint;

/**
 * Non-null constraint, which means that the value of the attribute cannot be empty, such as the
 * user's name attribute, since each user has a name, suitable for configuring non-null constraint.
 */
public class NotNullConstraint extends BaseConstraintItem {

  private static final long serialVersionUID = -6970332512136993837L;

  @Override
  public ConstraintTypeEnum getConstraintTypeEnum() {
    return ConstraintTypeEnum.NOT_NULL;
  }

  @Override
  public boolean checkIsLegal(Object value) {
    return value != null;
  }
}
