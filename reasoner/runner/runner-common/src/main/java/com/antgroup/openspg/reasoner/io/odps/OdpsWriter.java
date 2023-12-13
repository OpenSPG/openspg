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

import com.aliyun.odps.data.Record;
import com.aliyun.odps.tunnel.TableTunnel.UploadSession;
import com.aliyun.odps.tunnel.io.TunnelBufferedWriter;
import com.antgroup.openspg.reasoner.common.exception.OdpsException;
import com.antgroup.openspg.reasoner.io.ITableWriter;
import com.antgroup.openspg.reasoner.io.model.OdpsTableInfo;
import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;


@Slf4j(topic = "userlogger")
public class OdpsWriter implements ITableWriter {
    private int taskIndex;

    private OdpsTableInfo odpsTableInfo;

    private transient UploadSession        uploadSession = null;
    private transient TunnelBufferedWriter recordWriter  = null;

    private long writeCount = 0L;

    private static final int MAX_TRY_WRITE_TIMES = 5;

    /**
     * write buffer size
     */
    private static final long WRITER_BUFFER_SIZE = 32 * 1024 * 1024;
    /**
     * reset writer when count to 10M
     */
    private static final long WRITER_RESET_COUNT = 1000 * 10000;

    /**
     * init odps writer
     *
     * The odps writer will not commit the result himself,
     * You must ensure data commit by yourself.
     *
     * for example:
     *
     * // create upload session on driver
     * UploadSession session = OdpsUtils.getUploadSession(tableInfo);
     *
     * // set session id, makesure that the writer on each worker is under the same session id
     * tableInfo.setUploadSessionId(session.getId());
     *
     * // on worker, get writer and write data
     * ...(code on worker)
     *
     * // on driver, commit session
     * session.commit();
     */
    public void open(int taskIndex, int parallel, AbstractTableInfo tableInfo) {
        this.taskIndex = taskIndex;
        this.odpsTableInfo = (OdpsTableInfo) tableInfo;
        log.info("openOdpsWriter,index=" + this.taskIndex + ",odpsTableInfo=" + this.odpsTableInfo);
        this.uploadSession = OdpsUtils.tryGetUploadSession(this.odpsTableInfo, this.odpsTableInfo.getUploadSessionId(), taskIndex,
                parallel);
        resetWriter();
    }

    /**
     * write record
     */
    @Override
    public void write(Object[] data) {
        long c = this.writeCount++;

        if (1 == c % 10000) {
            log.info("index=" + this.taskIndex + ",write_odps_record[" + Arrays.toString(data) + "], write_count=" + c);
        }

        Record record = uploadSession.newRecord();
        record.set(data);

        // try five times at most
        int maxTryTimes = MAX_TRY_WRITE_TIMES;
        while (maxTryTimes-- > 0) {
            try {
                synchronized (this) {
                    recordWriter.write(record);
                }
                break;
            } catch (IOException e) {
                if (e.getLocalizedMessage().contains("MalformedDataStream")) {
                    log.error("write_odps_get_io_exception", e);
                    // io exception, reset
                    resetWriter();
                    continue;
                }
                throw new OdpsException("write_odps_record_error", e);
            }
        }
    }

    /**
     * close writer
     */
    @Override
    public void close() {
        closeWriter();
    }

    @Override
    public long writeCount() {
        return this.writeCount;
    }

    private void resetWriter() {
        closeWriter();
        recordWriter = OdpsUtils.tryCreateBufferRecordWriter(this.uploadSession);
        recordWriter.setBufferSize(WRITER_BUFFER_SIZE);
    }

    private void closeWriter() {
        if (null != recordWriter) {
            try {
                log.info("odps_writer_close, index=" + this.taskIndex + ", info=" + odpsTableInfo
                        + ", odps_write_count=" + writeCount);
                recordWriter.close();
            } catch (IOException e) {
                if (e.getLocalizedMessage().contains("MalformedDataStream")) {
                    log.error("close_writer_MalformedDataStream", e);
                    return;
                }
                log.error("close_writer_error", e);
                throw new OdpsException("close_writer_error", e);
            } finally {
                recordWriter = null;
                writeCount = 0L;
            }
        }
    }
}