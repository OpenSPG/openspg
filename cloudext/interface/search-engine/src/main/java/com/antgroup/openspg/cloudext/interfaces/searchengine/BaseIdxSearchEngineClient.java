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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.cloudext.interfaces.searchengine;

import com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.record.SPGRecord2IdxService;
import com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.record.impl.SPGRecord2IdxServiceImpl;
import com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.schema.SPGSchema2IdxService;
import com.antgroup.openspg.cloudext.interfaces.searchengine.adapter.schema.impl.SPGSchema2IdxServiceImpl;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxRecordManipulateCmd;
import com.antgroup.openspg.cloudext.interfaces.searchengine.cmd.IdxSchemaAlterCmd;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecordAlterItem;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxSchemaAlterItem;
import com.antgroup.openspg.server.core.builder.model.record.SPGRecordManipulateCmd;
import com.antgroup.openspg.server.core.schema.model.SPGSchemaAlterCmd;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseIdxSearchEngineClient
    implements SearchEngineClient, IdxDataDefinitionService, IdxDataManipulationService {

  private final SPGRecord2IdxService spgRecord2IdxService = new SPGRecord2IdxServiceImpl();
  private final SPGSchema2IdxService spgSchema2IdxService = new SPGSchema2IdxServiceImpl(this);

  @Override
  public int alterSchema(SPGSchemaAlterCmd cmd) {
    List<IdxSchemaAlterItem> alterItems = spgSchema2IdxService.generate(cmd.getSpgSchema());
    return alterSchema(new IdxSchemaAlterCmd(alterItems));
  }

  @Override
  public int manipulateRecord(SPGRecordManipulateCmd cmd) {
    List<IdxRecordAlterItem> alterItems =
        cmd.getAlterItems().stream()
            .flatMap(alterItem -> spgRecord2IdxService.build(alterItem).stream())
            .collect(Collectors.toList());
    return manipulateRecord(new IdxRecordManipulateCmd(alterItems));
  }
}
