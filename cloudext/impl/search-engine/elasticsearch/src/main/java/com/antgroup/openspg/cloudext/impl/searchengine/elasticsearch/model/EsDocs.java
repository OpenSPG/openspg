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

package com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.model;

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.common.model.base.BaseValObj;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

@Data
public class EsDocs extends BaseValObj {

  private List<EsDoc> docs;

  public List<IdxRecord> toIdxRecord() {
    if (CollectionUtils.isEmpty(docs)) {
      return new ArrayList<>(0);
    }
    return docs.stream()
        .map(
            esDoc ->
                new IdxRecord(
                    esDoc.get_index(), esDoc.get_id(), esDoc.get_score(), esDoc.get_source()))
        .collect(Collectors.toList());
  }
}
