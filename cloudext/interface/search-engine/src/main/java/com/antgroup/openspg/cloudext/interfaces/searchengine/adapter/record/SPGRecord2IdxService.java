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

package com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.record;

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecordAlterItem;
import com.antgroup.openspg.server.core.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.server.core.builder.model.record.SPGRecordAlterItem;
import java.util.List;

public interface SPGRecord2IdxService {

  /**
   * This method is used to build {@link IdxRecordAlterItem} from {@link SPGRecordAlterItem}
   *
   * @param item the alter item of {@link BaseSPGRecord SPGRecord}
   * @return the alter item of {@link IdxRecord IdxRecord}
   */
  List<IdxRecordAlterItem> build(SPGRecordAlterItem item);
}
