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

import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.BaseOntology;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;

/** Reference of the SPG type. */
public class SPGTypeRef extends BaseOntology
    implements WithBasicInfo<SPGTypeIdentifier>, WithSPGTypeEnum {

  private static final long serialVersionUID = -7765941631008945356L;

  /** Basic information of the SPG type. */
  private final BasicInfo<SPGTypeIdentifier> basicInfo;

  /** Enumeration value of the SPG type. */
  private final SPGTypeEnum spgTypeEnum;

  public SPGTypeRef(BasicInfo<SPGTypeIdentifier> basicInfo, SPGTypeEnum spgTypeEnum) {
    this.basicInfo = basicInfo;
    this.spgTypeEnum = spgTypeEnum;
  }

  @Override
  public BasicInfo<SPGTypeIdentifier> getBasicInfo() {
    return basicInfo;
  }

  @Override
  public SPGTypeEnum getSpgTypeEnum() {
    return spgTypeEnum;
  }
}
