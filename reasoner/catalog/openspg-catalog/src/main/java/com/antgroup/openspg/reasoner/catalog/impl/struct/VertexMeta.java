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
public class VertexMeta {
  @JSONField(name = "id")
  private Long id;

  @JSONField(name = "name")
  private String name;

  @JSONField(name = "attributeTypeDetailList")
  private List<PropertyMeta> attributeTypeDetailList;

  @JSONField(name = "relationTypeDetailList")
  private List<RelationTypeDetail> relationTypeDetailList;

  @JSONField(name = "inheritAttributeTypeDetailList")
  private List<PropertyMeta> inheritAttributeTypeDetailList;

  @JSONField(name = "entityCategory")
  private String entityCategory;

  /**
   * Getter method for property entityCategory.
   *
   * @return property value of entityCategory
   */
  public String getEntityCategory() {
    return entityCategory;
  }

  /**
   * Setter method for property entityCategory.
   *
   * @param entityCategory value to be assigned to property entityCategory
   */
  public void setEntityCategory(String entityCategory) {
    this.entityCategory = entityCategory;
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
   * Getter method for property relationTypeDetailList.
   *
   * @return property value of relationTypeDetailList
   */
  public List<RelationTypeDetail> getRelationTypeDetailList() {
    return relationTypeDetailList;
  }

  /**
   * Getter method for property relationTypeDetailList.
   *
   * @return property value of relationTypeDetailList
   */
  public List<PropertyMeta> getInheritAttributeTypeDetailList() {
    return inheritAttributeTypeDetailList;
  }

  /**
   * Setter method for property relationTypeDetailList.
   *
   * @param relationTypeDetailList value to be assigned to property relationTypeDetailList
   */
  public void setRelationTypeDetailList(List<RelationTypeDetail> relationTypeDetailList) {
    this.relationTypeDetailList = relationTypeDetailList;
  }
}
