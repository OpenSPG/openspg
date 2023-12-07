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

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** The transmission protocol used for basic data, with a format of key-value (KV). */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BuilderRecord extends BaseRecord {

  /** record id */
  private final String recordId;

  /** 表示spg类型或者关系 */
  private final BaseSPGIdentifier identifier;

  /** Key-value representation of a single data record. */
  private final Map<String, String> props;

  public String getPropValue(String propKey) {
    return props.get(propKey);
  }

  public BuilderRecord withNewProps(Map<String, String> newProps) {
    return new BuilderRecord(recordId, identifier, newProps);
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
