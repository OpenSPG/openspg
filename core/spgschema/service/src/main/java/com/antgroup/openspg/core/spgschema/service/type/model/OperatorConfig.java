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

package com.antgroup.openspg.core.spgschema.service.type.model;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.core.spgschema.model.type.OperatorKey;
import com.antgroup.openspg.core.spgschema.model.type.OperatorTypeEnum;

/**
 * The operator config of a spg type, for example, an EntityType will have a linking operator and a
 * fusing operator, and a ConceptType will have a fusing operator and a normalizing operator.
 */
public class OperatorConfig extends BaseValObj {

  private static final long serialVersionUID = 2831511535988285956L;

  /** The operator key, contains unique name and version of a operator. */
  private final OperatorKey operatorKey;

  /** The operator type. */
  private final OperatorTypeEnum operatorType;

  public OperatorConfig(OperatorKey operatorKey, OperatorTypeEnum operatorType) {
    this.operatorKey = operatorKey;
    this.operatorType = operatorType;
  }

  public OperatorKey getOperatorKey() {
    return operatorKey;
  }

  public OperatorTypeEnum getOperatorType() {
    return operatorType;
  }
}
