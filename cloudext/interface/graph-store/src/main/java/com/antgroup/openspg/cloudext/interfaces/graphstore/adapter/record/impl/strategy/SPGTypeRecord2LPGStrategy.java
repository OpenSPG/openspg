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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.strategy;

import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.VertexRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGRecordAlterItem;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.server.core.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.server.core.builder.model.record.SPGRecordAlterItem;
import java.util.List;

public class SPGTypeRecord2LPGStrategy extends BaseSPGRecord2LPGStrategy {

  @Override
  public List<LPGRecordAlterItem> translate(SPGRecordAlterItem item) {

    List<LPGRecordAlterItem> lpgRecordAlterItems = normalizeProperties(item);

    lpgRecordAlterItems.add(simplify(item));

    return lpgRecordAlterItems;
  }

  @Override
  protected LPGRecordAlterItem simplify(SPGRecordAlterItem alterItem) {
    BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) alterItem.getSpgRecord();
    VertexRecord vertexRecord = VertexRecordConvertor.toVertexRecord(advancedRecord);
    return new LPGRecordAlterItem(alterItem.getAlterOp(), vertexRecord);
  }
}
