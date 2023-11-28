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

package com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.util;

import com.antgroup.openspg.cloudext.interfaces.searchengine.IdxNameConvertor;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxRecordManipulateCmd;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxSchemaAlterCmd;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxSchema;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class IdxNameUtils {

  public static void convertIdxName(IdxRecordManipulateCmd cmd, IdxNameConvertor convertor) {
    cmd.getAlterItems()
        .forEach(
            alterItem -> {
              IdxRecord idxRecord = alterItem.getIdxRecord();
              idxRecord.setIdxName(convertor.convertIdxName(idxRecord.getIdxName()));
            });
  }

  public static void convertIdxName(IdxSchemaAlterCmd cmd, IdxNameConvertor convertor) {
    cmd.getAlterItems()
        .forEach(
            alterItem -> {
              IdxSchema idxSchema = alterItem.getIdxSchema();
              idxSchema.setIdxName(convertor.convertIdxName(idxSchema.getIdxName()));
            });
  }

  public static void restoreIdxName(List<IdxSchema> idxSchemas, IdxNameConvertor convertor) {
    if (CollectionUtils.isEmpty(idxSchemas)) {
      return;
    }
    idxSchemas.stream()
        .forEach(
            idxSchema -> {
              idxSchema.setIdxName(convertor.restoreIdxName(idxSchema.getIdxName()));
            });
  }
}
