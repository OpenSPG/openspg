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

package com.antgroup.openspg.builder.runner.local.runtime;

import com.antgroup.openspg.builder.core.runtime.BuilderRecordException;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.cloudext.interfaces.tablestore.TableFileHandler;
import com.antgroup.openspg.cloudext.interfaces.tablestore.TableStoreClient;
import com.antgroup.openspg.cloudext.interfaces.tablestore.TableStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.tablestore.cmd.TableFileCreateCmd;
import com.antgroup.openspg.cloudext.interfaces.tablestore.model.ColumnMeta;
import com.antgroup.openspg.cloudext.interfaces.tablestore.model.TableRecord;
import com.antgroup.openspg.server.common.model.datasource.connection.TableStoreConnectionInfo;

public class DefaultRecordCollector implements RecordCollector {

  private static final String RECORD_ID = "recordId";
  private static final String COMPONENT = "componentName";
  private static final String ERROR_MSG = "errorMsg";

  private final String tableName;
  private final TableStoreConnectionInfo connInfo;
  private volatile TableFileHandler tableFileHandler;

  public DefaultRecordCollector(String tableName, TableStoreConnectionInfo connInfo) {
    this.connInfo = connInfo;
    this.tableName = tableName;
  }

  private void init() {
    if (tableFileHandler == null) {
      synchronized (this) {
        if (tableFileHandler == null) {
          TableStoreClient tableStoreClient = TableStoreClientDriverManager.getClient(connInfo);
          tableFileHandler =
              tableStoreClient.create(
                  new TableFileCreateCmd(
                      tableName,
                      new ColumnMeta[] {
                        new ColumnMeta(RECORD_ID),
                        new ColumnMeta(COMPONENT),
                        new ColumnMeta(ERROR_MSG)
                      }));
        }
      }
    }
  }

  @Override
  public boolean haveCollected() {
    return tableFileHandler != null;
  }

  @Override
  public String getTableName() {
    if (tableFileHandler != null) {
      return tableFileHandler.getTableName();
    }
    return null;
  }

  @Override
  public void collectRecord(BuilderRecord record, BuilderRecordException e) {
    init();
    tableFileHandler.write(
        new TableRecord(
            new Object[] {record.getRecordId(), e.getProcessor().getName(), e.getMessage()}));
  }

  @Override
  public void close() throws Exception {
    if (tableFileHandler != null) {
      tableFileHandler.close();
    }
  }
}
