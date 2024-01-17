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
public class ConceptRule {
  @JSONField(name = "relation")
  private String relation;

  @JSONField(name = "objectMetaType")
  private String objectMetaType;

  @JSONField(name = "objectConcept")
  private String objectConcept;

  @JSONField(name = "dsl")
  private String dsl;

  /**
   * Getter method for property relation.
   *
   * @return property value of relation
   */
  public String getRelation() {
    return relation;
  }

  /**
   * Setter method for property relation.
   *
   * @param relation value to be assigned to property relation
   */
  public void setRelation(String relation) {
    this.relation = relation;
  }

  /**
   * Getter method for property objectMetaType.
   *
   * @return property value of objectMetaType
   */
  public String getObjectMetaType() {
    return objectMetaType;
  }

  /**
   * Setter method for property objectMetaType.
   *
   * @param objectMetaType value to be assigned to property objectMetaType
   */
  public void setObjectMetaType(String objectMetaType) {
    this.objectMetaType = objectMetaType;
  }

  /**
   * Getter method for property objectConcept.
   *
   * @return property value of objectConcept
   */
  public String getObjectConcept() {
    return objectConcept;
  }

  /**
   * Setter method for property objectConcept.
   *
   * @param objectConcept value to be assigned to property objectConcept
   */
  public void setObjectConcept(String objectConcept) {
    this.objectConcept = objectConcept;
  }

  /**
   * Getter method for property dsl.
   *
   * @return property value of dsl
   */
  public String getDsl() {
    return dsl;
  }

  /**
   * Setter method for property dsl.
   *
   * @param dsl value to be assigned to property dsl
   */
  public void setDsl(String dsl) {
    this.dsl = dsl;
  }
}
