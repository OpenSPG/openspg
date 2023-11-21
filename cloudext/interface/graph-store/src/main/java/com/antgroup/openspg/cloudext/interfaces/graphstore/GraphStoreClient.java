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

package com.antgroup.openspg.cloudext.interfaces.graphstore;

import com.antgroup.openspg.common.util.cloudext.CloudExtClient;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseSPGRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGRecordManipulateCmd;
import com.antgroup.openspg.core.spgschema.model.SPGSchemaAlterCmd;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;

public interface GraphStoreClient extends CloudExtClient {

  /**
   * Alter <tt>SPG</tt> schema.
   *
   * @param cmd command prompt of {@link BaseSPGType SPGType} alteration
   * @return <code>true</code> if alteration is success, <code>false</code> otherwise.
   */
  boolean alterSchema(SPGSchemaAlterCmd cmd);

  /**
   * Batch manipulate <tt>SPG</tt> records.
   *
   * @param cmd command prompt of {@link BaseSPGRecord SPGRecord} manipulation
   * @return <code>true</code> if all manipulations are success, <code>false</code> otherwise.
   */
  boolean manipulateRecord(SPGRecordManipulateCmd cmd);

  void close() throws Exception;
}
