/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.record.impl;

import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.SPGRecordAlterItem;
import com.antgroup.openspg.builder.model.record.SPGRecordTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.record.SPGRecord2IdxService;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecordAlterItem;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SPGRecord2IdxServiceImpl implements SPGRecord2IdxService {

  @Override
  public List<IdxRecordAlterItem> build(SPGRecordAlterItem item) {
    SPGRecordTypeEnum recordType = item.getSpgRecord().getRecordType();
    if (SPGRecordTypeEnum.RELATION.equals(recordType)) {
      return Collections.emptyList();
    }
    BaseAdvancedRecord spgRecord = (BaseAdvancedRecord) item.getSpgRecord();
    return Lists.newArrayList(
        new IdxRecordAlterItem(
            item.getAlterOp(),
            new IdxRecord(
                spgRecord.getName(),
                spgRecord.getId(),
                0.0,
                (Map) spgRecord.getStdStrPropertyValueMap())));
  }
}
