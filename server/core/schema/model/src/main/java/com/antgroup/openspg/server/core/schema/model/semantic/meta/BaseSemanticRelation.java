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

package com.antgroup.openspg.server.core.schema.model.semantic.meta;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import com.antgroup.openspg.server.core.schema.model.type.SPGTypeEnum;
import java.util.EnumSet;

public class BaseSemanticRelation extends BaseValObj {

  private static final long serialVersionUID = 7769133536707677102L;

  protected EnumSet<SPGTypeEnum> subjectType;

  protected EnumSet<SPGTypeEnum> objectType;

  public BaseSemanticRelation(EnumSet<SPGTypeEnum> subjectType, EnumSet<SPGTypeEnum> objectType) {
    this.subjectType = subjectType;
    this.objectType = objectType;
  }

  public EnumSet<SPGTypeEnum> getSubjectType() {
    return subjectType;
  }

  public EnumSet<SPGTypeEnum> getObjectType() {
    return objectType;
  }
}
