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

package com.antgroup.openspg.builder.model.record;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.schema.model.identifier.ConceptIdentifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class SPGPropertyValue extends BaseValObj {

  private static final String SEPARATOR = ",";

  /**
   * The standardized id value, including the link refers to the target entity id or concept mount
   * id
   */
  private String ids;

  /** raw attribute value */
  private String raw;

  /** Standardized attribute value */
  private Object std;

  public SPGPropertyValue(String raw) {
    this.raw = raw;
  }

  public List<String> getSplitIds() {
    if (StringUtils.isBlank(ids)) {
      return Collections.emptyList();
    }
    return Arrays.asList(ids.split(SEPARATOR));
  }

  public String getIds() {
    return ids;
  }

  public SPGPropertyValue setIds(String ids) {
    this.ids = ids;
    return this;
  }

  public String getRaw() {
    return raw;
  }

  public Object getStd() {
    return std;
  }

  public SPGPropertyValue setStd(Object std) {
    this.std = std;
    return this;
  }

  public void merge(SPGPropertyValue otherValue) {
    if (raw == null) {
      raw = otherValue.getRaw();
    } else {
      raw = raw + "," + otherValue.getRaw();
    }
  }

  public boolean contains(ConceptIdentifier conceptName) {
    return raw.contains(conceptName.getId());
  }

  public Object getStdOrRawValue() {
    return getStd() == null ? getRaw() : getStd();
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SPGPropertyValue{");
    sb.append("ids='").append(ids).append('\'');
    sb.append(", raw='").append(raw).append('\'');
    sb.append(", std=").append(std);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SPGPropertyValue)) {
      return false;
    }
    SPGPropertyValue that = (SPGPropertyValue) o;
    return Objects.equals(getIds(), that.getIds())
        && Objects.equals(getRaw(), that.getRaw())
        && Objects.equals(getStd(), that.getStd());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getIds(), getRaw(), getStd());
  }
}
