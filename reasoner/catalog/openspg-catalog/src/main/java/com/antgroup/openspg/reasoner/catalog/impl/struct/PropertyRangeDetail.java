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

package com.antgroup.openspg.reasoner.catalog.impl.struct;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class PropertyRangeDetail {
  @JSONField(name = "id")
  private long id;

  @JSONField(name = "rangeEntityName")
  private String rangeEntityName;

  @JSONField(name = "attrRangeTypeEnumCode")
  private String attrRangeTypeEnum;

  /**
   * Getter method for property rangeEntityName.
   *
   * @return property value of rangeEntityName
   */
  public String getRangeEntityName() {
    return rangeEntityName;
  }

  /**
   * Getter method for property attrRangeTypeEnum.
   *
   * @return property value of attrRangeTypeEnum
   */
  public String getAttrRangeTypeEnum() {
    return attrRangeTypeEnum;
  }

  /**
   * Setter method for property attrRangeTypeEnum.
   *
   * @param attrRangeTypeEnum value to be assigned to property attrRangeTypeEnum
   */
  public void setAttrRangeTypeEnum(String attrRangeTypeEnum) {
    this.attrRangeTypeEnum = attrRangeTypeEnum;
  }
}
