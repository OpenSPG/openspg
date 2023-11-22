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

package com.antgroup.openspg.builder.protocol;

import com.antgroup.openspg.server.schema.core.model.type.BaseSPGType;
import com.antgroup.openspg.server.schema.core.model.type.StandardType;
import java.util.Collections;
import java.util.List;

public class StandardRecord extends BaseAdvancedRecord {

  private final StandardType standardType;

  private final String standardId;

  private final List<SPGPropertyRecord> properties;

  public StandardRecord(
      StandardType standardType, String standardId, List<SPGPropertyRecord> properties) {
    super(SPGRecordTypeEnum.STANDARD);
    this.standardType = standardType;
    this.standardId = standardId;
    this.properties = properties;
  }

  @Override
  public BaseSPGType getSpgType() {
    return standardType;
  }

  @Override
  public List<BasePropertyRecord> getProperties() {
    return Collections.unmodifiableList(properties);
  }

  @Override
  public String getId() {
    return standardId;
  }

  @Override
  public List<SPGPropertyRecord> getSpgProperties() {
    return Collections.unmodifiableList(properties);
  }

  @Override
  public void addSpgProperties(SPGPropertyRecord record) {
    properties.add(record);
  }
}
