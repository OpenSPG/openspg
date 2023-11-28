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

package com.antgroup.openspg.builder.core.physical.sink;

import com.antgroup.openspg.builder.core.physical.process.CheckProcessor;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClient;
import com.antgroup.openspg.cloudext.interfaces.graphstore.GraphStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;
import com.antgroup.openspg.builder.model.pipeline.config.GraphStoreSinkNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.model.record.SPGPropertyRecord;
import com.antgroup.openspg.builder.model.record.SPGRecordAlterItem;
import com.antgroup.openspg.builder.model.record.SPGRecordManipulateCmd;
import com.antgroup.openspg.builder.model.record.SPGRecordTypeEnum;
import com.antgroup.openspg.schema.model.BasicInfo;
import com.antgroup.openspg.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.schema.model.predicate.Property;
import com.antgroup.openspg.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.schema.model.type.SPGTypeRef;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GraphStoreSinkWriter extends BaseSinkWriter<GraphStoreSinkNodeConfig> {

  private GraphStoreClient graphStoreClient;

  private SearchEngineClient searchEngineClient;

  private CheckProcessor checkProcessor;

  private static final SPGTypeRef TEXT_REF =
      new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier.parse("Text")), SPGTypeEnum.BASIC_TYPE);

  public GraphStoreSinkWriter(String id, String name, GraphStoreSinkNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(RuntimeContext context) throws Exception {
    if (context.getGraphStoreClient() != null) {
      graphStoreClient = context.getGraphStoreClient();
    } else {
      graphStoreClient =
          GraphStoreClientDriverManager.getClient(config.getGraphStoreConnectionInfo());
    }
    if (context.getSearchEngineClient() != null) {
      searchEngineClient = context.getSearchEngineClient();
    } else {
      searchEngineClient =
          SearchEngineClientDriverManager.getClient(config.getSearchEngineConnectionInfo());
    }
    checkProcessor = new CheckProcessor();
    checkProcessor.init(context);
  }

  @Override
  public void write(List<BaseRecord> records) {
    if (RecordAlterOperationEnum.UPSERT == context.getOperation()) {
      records = checkProcessor.process(records);
    }

    // replace standard property type which is of un-spreadable into text property type
    records.forEach(record -> replaceUnSpreadableStandardProperty((BaseSPGRecord) record));

    batchWriteToGraphStore(records);
    if (context.isEnableSearchEngine()) {
      batchWriteToSearchEngine(records);
    }
  }

  private void batchWriteToGraphStore(List<BaseRecord> records) {
    List<SPGRecordAlterItem> items =
        records.stream()
            .sorted(BaseRecord.ADVANCED_RECORD_FIRST_COMPARATOR)
            .map(record -> new SPGRecordAlterItem(context.getOperation(), (BaseSPGRecord) record))
            .collect(Collectors.toList());

    graphStoreClient.manipulateRecord(new SPGRecordManipulateCmd(items));
  }

  private void batchWriteToSearchEngine(List<BaseRecord> records) {
    List<SPGRecordAlterItem> items =
        records.stream()
            .map(record -> new SPGRecordAlterItem(context.getOperation(), (BaseSPGRecord) record))
            .collect(Collectors.toList());

    searchEngineClient.manipulateRecord(new SPGRecordManipulateCmd(items));
  }

  private void replaceUnSpreadableStandardProperty(BaseSPGRecord record) {
    if (SPGRecordTypeEnum.RELATION.equals(record.getRecordType())) {
      return;
    }

    record
        .getProperties()
        .forEach(
            property -> {
              if (!property.isSemanticProperty() || !property.getObjectTypeRef().isStandardType()) {
                return;
              }
              SPGTypeIdentifier spgTypeIdentifier =
                  property.getObjectTypeRef().getBaseSpgIdentifier();
              if (!context.getProjectSchema().getSpreadable(spgTypeIdentifier)) {
                Property propertyType = ((SPGPropertyRecord) property).getPropertyType();
                propertyType.setObjectTypeRef(TEXT_REF);
              }
            });
  }

  @Override
  public void close() throws Exception {}
}
