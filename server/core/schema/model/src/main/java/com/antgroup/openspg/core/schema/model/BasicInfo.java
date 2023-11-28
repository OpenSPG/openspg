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

package com.antgroup.openspg.core.schema.model;

import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.server.common.model.UserInfo;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.Objects;

/**
 * The basic information of the ontology model, including a unique name, Chinese name, and
 * description;
 */
public class BasicInfo<N extends BaseSPGIdentifier> extends BaseValObj {

  private static final long serialVersionUID = 5412142161259761200L;

  /** Unique name of spg type, property or relation. */
  private final N name;

  /** Chinese name. */
  private String nameZh;

  /** Description. */
  private String desc;

  /** Creator information. */
  private UserInfo creator;

  public BasicInfo(N name) {
    this(name, null, null);
  }

  public BasicInfo(N name, String nameZh, String desc) {
    this.name = name;
    this.nameZh = nameZh;
    this.desc = desc;
  }

  public N getName() {
    return name;
  }

  public String getNameZh() {
    return nameZh;
  }

  public String getDesc() {
    return desc;
  }

  public UserInfo getCreator() {
    return creator;
  }

  public void setCreator(UserInfo creator) {
    this.creator = creator;
  }

  public void setNameZh(String nameZh) {
    this.nameZh = nameZh;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BasicInfo)) {
      return false;
    }
    BasicInfo<?> basicInfo = (BasicInfo<?>) o;
    return Objects.equals(getName(), basicInfo.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }
}
