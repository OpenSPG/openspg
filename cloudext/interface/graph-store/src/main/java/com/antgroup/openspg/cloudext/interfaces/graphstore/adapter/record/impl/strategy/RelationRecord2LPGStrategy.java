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

import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.builder.model.record.SPGRecordAlterItem;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.EdgeRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGRecordAlterItem;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * Strategy for translation from alter item of {@link RelationRecord} to {@link
 * LPGRecordAlterItem}s.
 */
public class RelationRecord2LPGStrategy extends BaseSPGRecord2LPGStrategy {

  @Override
  public List<LPGRecordAlterItem> translate(SPGRecordAlterItem item) {
    return Lists.newArrayList(simplify(item));
  }

  @Override
  protected LPGRecordAlterItem simplify(SPGRecordAlterItem alterItem) {

    RelationRecord relationRecord = (RelationRecord) alterItem.getSpgRecord();

    return new LPGRecordAlterItem(
        alterItem.getAlterOp(), EdgeRecordConvertor.toEdgeRecord(relationRecord));
  }
}
