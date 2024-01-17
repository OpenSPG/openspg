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
public class PropertyMeta {
  @JSONField(name = "id")
  private Long id;

  @JSONField(name = "name")
  private String name;

  @JSONField(name = "attrRangeDetail")
  private PropertyRangeDetail propRange;

  @JSONField(name = "propertyCategoryEnum")
  private String category;

  @JSONField(name = "spreadable")
  private boolean spreadable;

  @JSONField(name = "transformerDetail")
  private TransformerDetail transformerDetail;

  @JSONField(name = "logicRule")
  private Rule logicRule;

  /**
   * Getter method for property logicalRule.
   *
   * @return property value of logicalRule
   */
  public Rule getLogicRule() {
    return logicRule;
  }

  /**
   * Setter method for property logicalRule.
   *
   * @param logicRule value to be assigned to property logicalRule
   */
  public void setLogicRule(Rule logicRule) {
    this.logicRule = logicRule;
  }

  /**
   * Getter method for property name.
   *
   * @return property value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Setter method for property name.
   *
   * @param name value to be assigned to property name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter method for property propRange.
   *
   * @return property value of propRange
   */
  public PropertyRangeDetail getPropRange() {
    return propRange;
  }

  /**
   * Setter method for property propRange.
   *
   * @param propRange value to be assigned to property propRange
   */
  public void setPropRange(PropertyRangeDetail propRange) {
    this.propRange = propRange;
  }

  /**
   * Getter method for property category.
   *
   * @return property value of category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Setter method for property category.
   *
   * @param category value to be assigned to property category
   */
  public void setCategory(String category) {
    this.category = category;
  }

  public boolean isSpreadable() {
    return spreadable;
  }

  /**
   * Setter method for property spreadable.
   *
   * @param spreadable value to be assigned to property spreadable
   */
  public void setSpreadable(boolean spreadable) {
    this.spreadable = spreadable;
  }

  /**
   * Getter method for property transformerDetail.
   *
   * @return property value of transformerDetail
   */
  public TransformerDetail getTransformerDetail() {
    return transformerDetail;
  }

  /**
   * Setter method for property transformerDetail.
   *
   * @param transformerDetail value to be assigned to property transformerDetail
   */
  public void setTransformerDetail(TransformerDetail transformerDetail) {
    this.transformerDetail = transformerDetail;
  }
}
