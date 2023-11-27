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
 * The unique constraint of attribute value means that there is only one attribute value, which is
 * different from the {@link MultiValConstraint}
 */
public class UniqueConstraint extends BaseConstraintItem {

  private static final long serialVersionUID = 2770310716268904304L;

  @Override
  public ConstraintTypeEnum getConstraintTypeEnum() {
    return ConstraintTypeEnum.UNIQUE;
  }

  @Override
  public boolean checkIsLegal(Object value) {
    return true;
  }
}
