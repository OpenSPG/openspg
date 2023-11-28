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

package com.antgroup.openspg.cloudext.interfaces.searchengine.cmd;

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecordAlterItem;
import com.antgroup.openspg.common.model.base.BaseCmd;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@AllArgsConstructor
public class IdxRecordManipulateCmd extends BaseCmd {

  private final List<IdxRecordAlterItem> alterItems;

  public boolean isEmpty() {
    return CollectionUtils.isEmpty(alterItems);
  }

  public Map<String, List<IdxRecord>> getUpsertIdxRecords() {
    return getIdxRecords(RecordAlterOperationEnum.UPSERT);
  }

  public Map<String, List<IdxRecord>> getDeleteIdxRecords() {
    return getIdxRecords(RecordAlterOperationEnum.DELETE);
  }

  private Map<String, List<IdxRecord>> getIdxRecords(RecordAlterOperationEnum alterOp) {
    return alterItems.stream()
        .filter(i -> i.getAlterOp().equals(alterOp))
        .map(IdxRecordAlterItem::getIdxRecord)
        .collect(Collectors.groupingBy(IdxRecord::getIdxName, Collectors.toList()));
  }
}
