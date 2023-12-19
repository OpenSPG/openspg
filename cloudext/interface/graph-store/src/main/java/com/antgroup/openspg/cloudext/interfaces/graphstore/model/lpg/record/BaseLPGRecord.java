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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Provides a generic base implementation for <tt>LPGRecord</tt>. A <tt>LPGRecord</tt> is with
 * multiple (or none) {@link LPGPropertyRecord LPGPropertyRecords}, and a {@link LPGRecordTypeEnum
 * LPGRecordTypeEnum} to mark type. All types of <tt>LPGRecord</tt> are the following:
 *
 * <ul>
 *   <li><code>VertexRecord</code>
 *   <li><code>EdgeRecord</code>
 * </ul>
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public abstract class BaseLPGRecord extends BaseValObj {

  private final LPGRecordTypeEnum recordType;

  private final List<LPGPropertyRecord> properties;

  public BaseLPGRecord(LPGRecordTypeEnum recordType, List<LPGPropertyRecord> properties) {
    this.recordType = recordType;

    if (CollectionUtils.isEmpty(properties)) {
      this.properties = new ArrayList<>(0);
    } else {
      this.properties = properties;
    }
  }

  /**
   * Convert all {@link LPGPropertyRecord LPGPropertyRecords} in the <tt>LPGRecord</tt> into a map.
   *
   * <p>The key of map is name of property, and the value is value of property.
   *
   * @return a map
   */
  public Map<String, Object> toPropertyMap() {
    return properties.stream()
        .filter(x -> x.getValue() != null)
        .collect(Collectors.toMap(LPGPropertyRecord::getName, LPGPropertyRecord::getValue));
  }

  /**
   * Convert all {@link LPGPropertyRecord LPGPropertyRecords} in the <tt>LPGRecord</tt> and
   * <tt>LPGRecord</tt>'identy of into a map.
   *
   * @return a map
   */
  public abstract Map<String, Object> toPropertyMapWithId();
}
