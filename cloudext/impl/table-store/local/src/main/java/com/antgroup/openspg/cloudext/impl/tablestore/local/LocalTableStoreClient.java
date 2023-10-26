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

package com.antgroup.openspg.cloudext.impl.tablestore.local;

import com.antgroup.openspg.cloudext.interfaces.tablestore.TableFileHandler;
import com.antgroup.openspg.cloudext.interfaces.tablestore.TableStoreClient;
import com.antgroup.openspg.cloudext.interfaces.tablestore.cmd.TableFileCreateCmd;
import com.antgroup.openspg.cloudext.interfaces.tablestore.model.ColumnMeta;
import com.antgroup.openspg.common.model.datasource.connection.TableStoreConnectionInfo;
import com.antgroup.openspg.common.util.struct.tuple.Tuple2;

import com.opencsv.CSVWriter;
import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


public class LocalTableStoreClient implements TableStoreClient {

    @Getter
    private final TableStoreConnectionInfo connInfo;
    private final String localRootDir;

    public LocalTableStoreClient(TableStoreConnectionInfo connInfo) {
        this.connInfo = connInfo;
        this.localRootDir = (String) connInfo.getNotNullParam("localDir");
        new File("./" + localRootDir).mkdirs();
    }

    @Override
    public TableFileHandler create(TableFileCreateCmd cmd) {
        try {
            Tuple2<CSVWriter, String> tuple2 = buildCsvWriter(cmd);
            return new LocalTableFileHandler(tuple2.first, tuple2.second);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Tuple2<CSVWriter, String> buildCsvWriter(TableFileCreateCmd cmd) throws IOException {
        String filePath = "./" + localRootDir + "/" + cmd.getName() + ".csv";
        CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath));

        String[] columns = Arrays.stream(cmd.getColumns())
            .map(ColumnMeta::getName)
            .toArray(String[]::new);
        csvWriter.writeNext(columns);
        return Tuple2.of(csvWriter, filePath);
    }
}
