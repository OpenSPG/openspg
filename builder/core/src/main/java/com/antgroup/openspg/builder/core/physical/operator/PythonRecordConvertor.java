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

package com.antgroup.openspg.builder.core.physical.operator;

import com.antgroup.openspg.builder.core.physical.operator.protocol.PythonRecord;
import com.antgroup.openspg.builder.core.runtime.BuilderCatalog;
import com.antgroup.openspg.builder.core.strategy.linking.RecordLinking;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.VertexRecordConvertor;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.Map;

public class PythonRecordConvertor {

  public static BaseAdvancedRecord toAdvancedRecord(
      PythonRecord pythonRecord, RecordLinking recordLinking, BuilderCatalog catalog) {
    String recordId = pythonRecord.getId();
    if (StringUtils.isBlank(recordId)) {
      return null;
    }

    BaseSPGType spgType =
        catalog.getSPGType(SPGTypeIdentifier.parse(pythonRecord.getSpgTypeName()));
    if (spgType == null) {
      return null;
    }
    BaseAdvancedRecord advancedRecord =
        VertexRecordConvertor.toAdvancedRecord(spgType, recordId, pythonRecord.getProperties());
    recordLinking.linking(advancedRecord);
    return advancedRecord;
  }

  public static PythonRecord toPythonRecord(BaseAdvancedRecord advancedRecord) {
    Map<String, String> stdStrPropertyValueMap = advancedRecord.getStdStrPropertyValueMap();
    stdStrPropertyValueMap.put("id", advancedRecord.getId());
    return new PythonRecord()
        .setSpgTypeName(advancedRecord.getName())
        .setProperties(stdStrPropertyValueMap);
  }
}
