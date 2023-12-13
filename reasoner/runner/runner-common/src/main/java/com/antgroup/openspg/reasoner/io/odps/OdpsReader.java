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
package com.antgroup.openspg.reasoner.io.odps;

import com.aliyun.odps.Column;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.data.RecordReader;
import com.aliyun.odps.tunnel.TableTunnel.DownloadSession;
import com.antgroup.openspg.reasoner.common.exception.OdpsException;
import com.antgroup.openspg.reasoner.common.table.Field;
import com.antgroup.openspg.reasoner.io.ITableReader;
import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;
import com.antgroup.openspg.reasoner.io.model.OdpsTableInfo;
import com.antgroup.openspg.reasoner.io.model.ReadRange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Slf4j(topic = "userlogger")
public class OdpsReader implements ITableReader {

    protected final int MAX_ODPS_READER_COUNT = 500 * 10000;

    protected int index;

    protected long readCount;

    protected transient Map<OdpsTableInfo, ReadRange> tableReadRangeMap = new HashMap<>();

    protected final Map<OdpsTableInfo, DownloadSession> downloadSessionMap;

    public OdpsReader(Map<OdpsTableInfo, DownloadSession> downloadSessionMap) {
        this.downloadSessionMap = downloadSessionMap;
    }

    private DownloadSession getDownloadSession(OdpsTableInfo odpsTableInfo) {
        return this.downloadSessionMap.get(odpsTableInfo);
    }

    /**
     * open odps reader
     */
    @Override
    public void init(int index, int parallel, int nowRound, int allRound, List<AbstractTableInfo> tableInfoList) {
        this.index = index;
        Map<OdpsTableInfo, Long> tableCountMap = new HashMap<>();
        for (AbstractTableInfo tableInfo : tableInfoList) {
            OdpsTableInfo odpsTableInfo = (OdpsTableInfo) tableInfo;
            long count = getDownloadSession(odpsTableInfo).getRecordCount();
            tableCountMap.put(odpsTableInfo, count);
        }
        this.tableReadRangeMap = OdpsUtils.getReadRange(parallel, index, allRound, nowRound, tableCountMap);

        // init iterator
        this.nowReadTableIt = this.tableReadRangeMap.entrySet().iterator();
        this.nowReadRange = null;
        this.readCount = 0L;
    }

    /**
     * close odps reader
     */
    @Override
    public void close() {
        log.info("close odps reader, index=" + this.index + ", readCount=" + this.readCount);
    }

    private Iterator<Map.Entry<OdpsTableInfo, ReadRange>> nowReadTableIt;

    private OdpsTableInfo        nowOdpsTableInfo          = null;
    private RecordReader         nowRecordReader           = null;
    private Map<String, Integer> columnName2ResultIndexMap = null;
    private ReadRange            nowReadRange              = null;
    private long                 nowReadCount              = 0;

    @Override
    public boolean hasNext() {
        if (nowReaderHasNext()) {
            return true;
        }
        return nowReadTableIt.hasNext();
    }

    @Override
    public Object[] next() {
        this.readCount++;
        if (nowReaderHasNext()) {
            return readRecord();
        }

        Map.Entry<OdpsTableInfo, ReadRange> entry = this.nowReadTableIt.next();
        this.nowOdpsTableInfo = entry.getKey();
        this.nowReadRange = entry.getValue();
        this.nowReadCount = 0;
        this.nowRecordReader = OdpsUtils.tryOpenRecordReader(getDownloadSession(this.nowOdpsTableInfo),
                this.nowReadRange.getStart(), this.nowReadRange.getEnd());
        this.initColumnName2ResultIndexMap();
        return readRecord();
    }

    private boolean nowReaderHasNext() {
        return null != this.nowReadRange && this.nowReadCount < this.nowReadRange.getCount();
    }

    private void initColumnName2ResultIndexMap() {
        if (CollectionUtils.isEmpty(this.nowOdpsTableInfo.getColumns())) {
            columnName2ResultIndexMap = null;
            return;
        }
        columnName2ResultIndexMap = new HashMap<>();
        int resultSize = this.nowOdpsTableInfo.getColumns().size();
        for (int i = 0; i < resultSize; ++i) {
            Field field = this.nowOdpsTableInfo.getColumns().get(i);
            columnName2ResultIndexMap.put(field.getName(), i);
        }
    }

    private Object[] readRecord() {
        Record record;
        try {
            record = this.nowRecordReader.read();
            nowReadCount++;
            if (nowReadCount > MAX_ODPS_READER_COUNT) {
                // reset reader when read a lot of datas
                this.nowRecordReader = OdpsUtils.tryOpenRecordReader(getDownloadSession(this.nowOdpsTableInfo),
                        this.nowReadRange.getStart(), this.nowReadRange.getEnd());
                this.initColumnName2ResultIndexMap();
            }
        } catch (IOException e) {
            throw new OdpsException("read odps record error", e);
        }

        Column[] columns = record.getColumns();

        // convert type
        Object[] result = new Object[null == this.columnName2ResultIndexMap ? columns.length : this.columnName2ResultIndexMap.size()];
        for (int i = 0; i < columns.length; ++i) {
            Column column = columns[i];
            int resultIndex = i;
            if (null != this.columnName2ResultIndexMap) {
                Integer integer = this.columnName2ResultIndexMap.get(column.getName());
                if (null == integer) {
                    continue;
                }
                resultIndex = integer;
            }
            switch (column.getTypeInfo().getOdpsType()) {
                case STRING:
                case VARCHAR:
                case CHAR:
                    result[resultIndex] = record.getString(i);
                    break;
                case FLOAT:
                    result[resultIndex] = record.getDouble(i).floatValue();
                    break;
                case DOUBLE:
                    result[resultIndex] = record.getDouble(i);
                    break;
                case INT:
                    result[resultIndex] = record.getBigint(i).intValue();
                    break;
                case SMALLINT:
                    result[resultIndex] = record.getBigint(i).shortValue();
                    break;
                case TINYINT:
                    result[resultIndex] = record.getBigint(i).byteValue();
                    break;
                case BIGINT:
                    result[resultIndex] = record.getBigint(i);
                    break;
                case BOOLEAN:
                    result[resultIndex] = record.getBoolean(i);
                    break;
                default:
                    result[resultIndex] = record.get(i);
                    break;
            }
        }
        return result;
    }

}