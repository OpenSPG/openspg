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
package com.antgroup.openspg.reasoner.warehouse.common.config;

import java.io.Serializable;

public class StartVertexConfig implements Serializable {
  private String vertexTypeName;
  private String bizId;
  private String aliasName;
  private int maxDepth;

  /**
   * getter
   *
   * @return
   */
  public String getVertexTypeName() {
    return vertexTypeName;
  }

  /**
   * setter
   *
   * @param vertexTypeName
   */
  public void setVertexTypeName(String vertexTypeName) {
    this.vertexTypeName = vertexTypeName;
  }

  /**
   * getter
   *
   * @return
   */
  public String getBizId() {
    return bizId;
  }

  /**
   * setter
   *
   * @param bizId
   */
  public void setBizId(String bizId) {
    this.bizId = bizId;
  }

  /**
   * getter
   *
   * @return
   */
  public String getAliasName() {
    return aliasName;
  }

  /**
   * setter
   *
   * @param aliasName
   */
  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
  }

  /**
   * getter
   *
   * @return
   */
  public int getMaxDepth() {
    return maxDepth;
  }

  /**
   * setter
   *
   * @param maxDepth
   */
  public void setMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
  }
}
