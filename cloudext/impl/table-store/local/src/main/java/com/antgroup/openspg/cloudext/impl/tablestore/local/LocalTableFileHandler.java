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
import com.antgroup.openspg.cloudext.interfaces.tablestore.model.TableRecord;

import com.opencsv.CSVWriter;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


public class LocalTableFileHandler implements TableFileHandler {

    private final CSVWriter csvWriter;
    private final String tableName;
    private final static int DEFAULT_BUFFER_SIZE = 1000;
    private final Queue<String[]> buffer;

    public LocalTableFileHandler(CSVWriter csvWriter, String tableName) {
        this.csvWriter = csvWriter;
        this.tableName = tableName;
        buffer = new ArrayBlockingQueue<>(DEFAULT_BUFFER_SIZE);
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public int write(TableRecord record) {
        if (record.getValues() != null && record.getValues().length > 0) {
            buffer.add(Arrays.stream(record.getValues())
                .map(Object::toString)
                .toArray(String[]::new));
            if (buffer.size() == DEFAULT_BUFFER_SIZE) {
                flushBuffer();
            }
            return 1;
        }
        return 0;
    }

    @Override
    public int batchWrite(List<TableRecord> records) {
        for (TableRecord record : records) {
            write(record);
        }
        return records.size();
    }

    private synchronized void flushBuffer() {
        while (!buffer.isEmpty()) {
            csvWriter.writeNext(buffer.poll());
        }
    }

    @Override
    public void close() throws Exception {
        if (csvWriter != null) {
            flushBuffer();
            csvWriter.close();
        }
    }
}
