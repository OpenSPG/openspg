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
package com.antgroup.openspg.reasoner.loader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.exception.SystemError;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "userlogger")
public class DiskStartIdRecorder extends MemStartIdRecoder {

    private final String id;

    /**
     * start id recorder on disk
     */
    public DiskStartIdRecorder(String id) {
        this.id = id;
        Utils.deletePath(getFileName(id));
        internalIt = null;
    }

    @Override
    public void flush() {
        try {
            write(getFileName(id), Lists.newArrayList(startIdSet));
        } catch (Exception e) {
            throw new SystemError("write start id error", e);
        } finally {
            startIdSet = null;
        }
        internalIt = getStartIdIterator(id, null);
    }

    @Override
    public long getStartIdCount() {
        long count = 0;
        String filePath = getFileName(id);
        try {
            DataInputStream dataInputStream = new DataInputStream(Files.newInputStream(Paths.get(filePath)));
            long size = dataInputStream.readLong();
            dataInputStream.close();
            count += size;
        } catch (Throwable e) {
            throw new SystemError("load start id file=" + filePath, e);
        }
        return count;
    }

    @Override
    public boolean hasNext() {
        if (null == internalIt) {
            return false;
        }
        return internalIt.hasNext();
    }

    @Override
    public IVertexId next() {
        return internalIt.next();
    }

    private final static String START_ID_PATH_PREFIX = "/tmp/holmes/start_id/";

    private String getFileName(String id) {
        return START_ID_PATH_PREFIX + id + "/start_id.bin";
    }

    private void write(String fileName, List<IVertexId> idList) throws Exception {
        File file = new File(fileName);
        if (file.exists()) {
            Utils.deletePath(fileName);
        }
        file.getParentFile().mkdirs();

        FileOutputStream outputStream = new FileOutputStream(fileName, true);

        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
        bb.putLong(idList.size());
        outputStream.write(bb.array());

        for (IVertexId id : idList) {
            byte[] idBytes = id.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(idBytes.length + Integer.BYTES);
            byteBuffer.putInt(idBytes.length);
            byteBuffer.put(idBytes);
            outputStream.write(byteBuffer.array());
        }
        outputStream.close();
    }

    private Iterator<IVertexId> getStartIdIterator(String id, Predicate<IVertexId> idPredicate) {
        List<DataInputStream> idInputStreamList = new ArrayList<>();

        String filePath = getFileName(id);
        try {
            DataInputStream dataInputStream = new DataInputStream(Files.newInputStream(Paths.get(filePath)));
            idInputStreamList.add(dataInputStream);
        } catch (Exception e) {
            throw new SystemError("can not found start id file=" + filePath, e);
        }

        return new Iterator<IVertexId>() {
            private final List<DataInputStream> inputStreamList = idInputStreamList;

            private int nowStreamIndex = 0;
            private long nowSize = -1;

            private IVertexId cachedId = null;

            @Override
            public boolean hasNext() {
                if (null == cachedId) {
                    cachedId = getNextId();
                }
                return null != cachedId;
            }

            @Override
            public IVertexId next() {
                if (null == cachedId) {
                    cachedId = getNextId();
                }
                IVertexId rst = cachedId;
                cachedId = null;
                return rst;
            }

            private IVertexId getNextId() {
                while (true) {
                    DataInputStream dataInputStream = getNextDataInputStream();
                    if (null == dataInputStream) {
                        return null;
                    }

                    byte[] idBytes;
                    try {
                        int idSize = dataInputStream.readInt();
                        idBytes = new byte[idSize];
                        int r = dataInputStream.read(idBytes);
                        if (r < 0) {
                            return null;
                        }
                    } catch (Exception e) {
                        throw new SystemError("read error", e);
                    }
                    IVertexId id = IVertexId.from(idBytes);
                    nowSize--;
                    if (null != idPredicate && !idPredicate.test(id)) {
                        continue;
                    }
                    return id;
                }
            }

            private DataInputStream getNextDataInputStream() {
                while (nowStreamIndex < inputStreamList.size()) {
                    if (nowSize > 0) {
                        return inputStreamList.get(nowStreamIndex);
                    }

                    if (nowSize < 0) {
                        DataInputStream dataInputStream = inputStreamList.get(nowStreamIndex);
                        try {
                            nowSize = dataInputStream.readLong();
                        } catch (IOException e) {
                            throw new SystemError("read error", e);
                        }
                    }
                    if (nowSize == 0) {
                        nowStreamIndex++;
                        nowSize = -1;
                        continue;
                    }
                    return inputStreamList.get(nowStreamIndex);
                }
                return null;
            }
        };
    }
}