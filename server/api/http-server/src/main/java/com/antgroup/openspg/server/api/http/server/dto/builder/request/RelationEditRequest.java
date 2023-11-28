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

package com.antgroup.openspg.server.api.http.server.dto.builder.request;

import java.util.Map;

public class RelationEditRequest extends BaseEditRequest {

  private String sName;
  private String relation;
  private String oName;

  private String srcId;
  private String dstId;

  private Map<String, String> properties;

  public String getsName() {
    return sName;
  }

  public RelationEditRequest setsName(String sName) {
    this.sName = sName;
    return this;
  }

  public String getRelation() {
    return relation;
  }

  public RelationEditRequest setRelation(String relation) {
    this.relation = relation;
    return this;
  }

  public String getoName() {
    return oName;
  }

  public RelationEditRequest setoName(String oName) {
    this.oName = oName;
    return this;
  }

  public String getSrcId() {
    return srcId;
  }

  public RelationEditRequest setSrcId(String srcId) {
    this.srcId = srcId;
    return this;
  }

  public String getDstId() {
    return dstId;
  }

  public RelationEditRequest setDstId(String dstId) {
    this.dstId = dstId;
    return this;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public RelationEditRequest setProperties(Map<String, String> properties) {
    this.properties = properties;
    return this;
  }

  public String getName() {
    return String.format("(%s)->[%s]->(%s)", sName, relation, oName);
  }
}
