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

package com.antgroup.openspg.reasoner.catalog.impl.struct;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class TransformerDetail {

  @JSONField(name = "transformerCategoryEnum")
  private String transformerCategoryEnum;

  /**
   * Getter method for property transformerCategoryEnum.
   *
   * @return property value of transformerCategoryEnum
   */
  public String getTransformerCategoryEnum() {
    return transformerCategoryEnum;
  }

  /**
   * Setter method for property transformerCategoryEnum.
   *
   * @param transformerCategoryEnum value to be assigned to property transformerCategoryEnum
   */
  public void setTransformerCategoryEnum(String transformerCategoryEnum) {
    this.transformerCategoryEnum = transformerCategoryEnum;
  }
}
