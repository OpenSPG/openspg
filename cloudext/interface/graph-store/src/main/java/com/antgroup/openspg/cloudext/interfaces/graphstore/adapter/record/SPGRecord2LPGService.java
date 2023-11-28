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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.BaseLPGRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGRecordAlterItem;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.SPGRecordAlterItem;
import java.util.List;

public interface SPGRecord2LPGService {

  /**
   * This method is used to convert {@link SPGRecordAlterItem} into {@link LPGRecordAlterItem}
   *
   * @param item the alter item of {@link BaseSPGRecord SPGRecord}
   * @return the alter item of {@link BaseLPGRecord LPGRecord}
   */
  List<LPGRecordAlterItem> convert(SPGRecordAlterItem item);
}
