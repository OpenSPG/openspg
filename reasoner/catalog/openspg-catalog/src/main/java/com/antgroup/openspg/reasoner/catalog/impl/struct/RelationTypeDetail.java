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
import java.util.List;
import lombok.Data;

@Data
public class RelationTypeDetail {
  @JSONField(name = "id")
  private Long id;

  @JSONField(name = "name")
  private String name;

  @JSONField(name = "startEntityTypeDetail")
  private VertexMeta startEntityType;

  @JSONField(name = "endEntityTypeDetail")
  private VertexMeta endEntityType;

  @JSONField(name = "attributeTypeDetailList")
  private List<PropertyMeta> attributeTypeDetailList;

  @JSONField(name = "relationDirectionEnumCode")
  private String relationDirectionEnum;

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
  public Long getId() {
    return id;
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
   * Getter method for property startEntityType.
   *
   * @return property value of startEntityType
   */
  public VertexMeta getStartEntityType() {
    return startEntityType;
  }

  /**
   * Setter method for property startEntityType.
   *
   * @param startEntityType value to be assigned to property startEntityType
   */
  public void setStartEntityType(VertexMeta startEntityType) {
    this.startEntityType = startEntityType;
  }

  /**
   * Getter method for property endEntityType.
   *
   * @return property value of endEntityType
   */
  public VertexMeta getEndEntityType() {
    return endEntityType;
  }

  /**
   * Setter method for property endEntityType.
   *
   * @param endEntityType value to be assigned to property endEntityType
   */
  public void setEndEntityType(VertexMeta endEntityType) {
    this.endEntityType = endEntityType;
  }

  /**
   * Getter method for property attributeTypeDetailList.
   *
   * @return property value of attributeTypeDetailList
   */
  public List<PropertyMeta> getAttributeTypeDetailList() {
    return attributeTypeDetailList;
  }

  /**
   * Setter method for property attributeTypeDetailList.
   *
   * @param attributeTypeDetailList value to be assigned to property attributeTypeDetailList
   */
  public void setAttributeTypeDetailList(List<PropertyMeta> attributeTypeDetailList) {
    this.attributeTypeDetailList = attributeTypeDetailList;
  }

  /**
   * Getter method for property relationDirectionEnum.
   *
   * @return property value of relationDirectionEnum
   */
  public String getRelationDirectionEnum() {
    return relationDirectionEnum;
  }

  /**
   * Setter method for property relationDirectionEnum.
   *
   * @param relationDirectionEnum value to be assigned to property relationDirectionEnum
   */
  public void setRelationDirectionEnum(String relationDirectionEnum) {
    this.relationDirectionEnum = relationDirectionEnum;
  }
}
