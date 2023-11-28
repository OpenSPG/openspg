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

package com.antgroup.openspg.schema.http.client.request;

import com.antgroup.openspg.common.model.base.BaseRequest;

/** The query request of relation. */
public class RelationRequest extends BaseRequest {

  private static final long serialVersionUID = -6235950903174303816L;

  /** The unique name of subject spg type in spo triple. */
  private String sName;

  /** The name of relation type. */
  private String relation;

  /** The unique name of object spg type in spo triple */
  private String oName;

  public String getsName() {
    return sName;
  }

  public RelationRequest setsName(String sName) {
    this.sName = sName;
    return this;
  }

  public String getRelation() {
    return relation;
  }

  public RelationRequest setRelation(String relation) {
    this.relation = relation;
    return this;
  }

  public String getoName() {
    return oName;
  }

  public RelationRequest setoName(String oName) {
    this.oName = oName;
    return this;
  }

  public static RelationRequest parse(String spgName) {
    String[] splits = spgName.split("_");
    return new RelationRequest().setsName(splits[0]).setRelation(splits[1]).setoName(splits[2]);
  }

  @Override
  public String toString() {
    return String.format("%s_%s_%s", sName, relation, oName);
  }
}
