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

package com.antgroup.openspg.reasoner.thinker.logic.graph;

import com.alibaba.fastjson.JSON;
import java.util.List;
import java.util.Objects;

public class CombinationEntity extends Element {
  private List<Entity> entityList;
  private String alias;

  public CombinationEntity() {}

  public CombinationEntity(List<Entity> entityList) {
    this.entityList = entityList;
  }

  public CombinationEntity(List<Entity> entityList, String alias) {
    this.entityList = entityList;
    this.alias = alias;
  }

  /**
   * Getter method for property <tt>entityList</tt>.
   *
   * @return property value of entityList
   */
  public List<Entity> getEntityList() {
    return entityList;
  }

  /**
   * Setter method for property <tt>entityList</tt>.
   *
   * @param entityList value to be assigned to property entityList
   */
  public void setEntityList(List<Entity> entityList) {
    this.entityList = entityList;
  }

  /**
   * Getter method for property <tt>alias</tt>.
   *
   * @return property value of alias
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Setter method for property <tt>alias</tt>.
   *
   * @param alias value to be assigned to property alias
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CombinationEntity)) {
      return false;
    }
    CombinationEntity that = (CombinationEntity) o;
    return Objects.equals(entityList, that.entityList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityList, alias);
  }

  @Override
  public String toString() {
    return JSON.toJSONString(entityList);
  }
}
