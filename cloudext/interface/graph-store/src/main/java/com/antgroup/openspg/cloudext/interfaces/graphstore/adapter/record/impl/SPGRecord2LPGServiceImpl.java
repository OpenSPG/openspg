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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl;

import com.antgroup.openspg.builder.model.record.SPGRecordAlterItem;
import com.antgroup.openspg.builder.model.record.SPGRecordTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.graphstore.BaseLPGGraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.SPGRecord2LPGService;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.strategy.ConceptRecord2LPGStrategy;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.strategy.RelationRecord2LPGStrategy;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.strategy.SPGRecord2LPGStrategy;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.strategy.SPGTypeRecord2LPGStrategy;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGRecordAlterItem;
import com.antgroup.openspg.server.common.model.exception.GraphStoreException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SPGRecord2LPGServiceImpl implements SPGRecord2LPGService {

  private final BaseLPGGraphStoreClient lpgGraphStoreClient;

  public final Map<SPGRecordTypeEnum, SPGRecord2LPGStrategy> registeredStrategy = new HashMap<>();

  private void init() {
    SPGTypeRecord2LPGStrategy spgTypeRecord2LpgStrategy = new SPGTypeRecord2LPGStrategy();
    registeredStrategy.put(SPGRecordTypeEnum.ENTITY, spgTypeRecord2LpgStrategy);
    registeredStrategy.put(SPGRecordTypeEnum.EVENT, spgTypeRecord2LpgStrategy);
    registeredStrategy.put(SPGRecordTypeEnum.STANDARD, spgTypeRecord2LpgStrategy);

    ConceptRecord2LPGStrategy conceptRecord2LpgStrategy = new ConceptRecord2LPGStrategy();
    registeredStrategy.put(SPGRecordTypeEnum.CONCEPT, conceptRecord2LpgStrategy);

    RelationRecord2LPGStrategy relationRecord2LpgStrategy = new RelationRecord2LPGStrategy();
    registeredStrategy.put(SPGRecordTypeEnum.RELATION, relationRecord2LpgStrategy);

    registeredStrategy.values().forEach(x -> x.setLpgGraphStoreClient(this.lpgGraphStoreClient));
  }

  public SPGRecord2LPGServiceImpl(BaseLPGGraphStoreClient lpgGraphStoreClient) {
    this.lpgGraphStoreClient = lpgGraphStoreClient;
    init();
  }

  @Override
  public List<LPGRecordAlterItem> convert(SPGRecordAlterItem item) {
    SPGRecordTypeEnum recordType = item.getSpgRecord().getRecordType();
    // select strategy by type of SPG record.
    SPGRecord2LPGStrategy spgRecord2LpgStrategy = registeredStrategy.get(recordType);
    if (spgRecord2LpgStrategy == null) {
      throw GraphStoreException.unexpectedSPGRecordTypeEnum(recordType);
    }
    // do translate from SPG to LPG
    return spgRecord2LpgStrategy.translate(item);
  }
}
