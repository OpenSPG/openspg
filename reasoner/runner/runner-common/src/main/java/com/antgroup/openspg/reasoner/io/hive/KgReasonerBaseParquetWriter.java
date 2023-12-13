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
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io.hive;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.OutputFile;
import org.apache.parquet.schema.MessageType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class KgReasonerBaseParquetWriter extends ParquetWriter<Group> {

    /**
     * Creates a Builder for configuring ParquetWriter with the example object
     * model.
     *
     * @param file the output file to create
     * @return a {@link Builder} to create a {@link ParquetWriter}
     */
    public static Builder builder(OutputFile file) {
        return new Builder(file);
    }

    /**
     * Create a new {@link KgReasonerBaseParquetWriter}.
     *
     * @param file                 The file name to write to.
     * @param writeSupport         The schema to write with.
     * @param compressionCodecName Compression code to use, or CompressionCodecName.UNCOMPRESSED
     * @param blockSize            the block size threshold.
     * @param pageSize             See parquet write up. Blocks are subdivided into pages for alignment and other purposes.
     * @param enableDictionary     Whether to use a dictionary to compress columns.
     * @param conf                 The Configuration to use.
     * @throws IOException
     */
    KgReasonerBaseParquetWriter(Path file, WriteSupport<Group> writeSupport,
                                CompressionCodecName compressionCodecName,
                                int blockSize, int pageSize, boolean enableDictionary,
                                boolean enableValidation,
                                ParquetProperties.WriterVersion writerVersion,
                                Configuration conf)
            throws IOException {
        super(file, writeSupport, compressionCodecName, blockSize, pageSize,
                pageSize, enableDictionary, enableValidation, writerVersion, conf);
    }

    /**
     * Builder实现
     */
    public static class Builder extends ParquetWriter.Builder<Group, Builder> {
        private MessageType         type          = null;
        private Map<String, String> extraMetaData = new HashMap<String, String>();

        /**
         * Builder
         *
         * @param file
         */
        private Builder(OutputFile file) {
            super(file);
        }

        /**
         * withType
         *
         * @param type
         * @return
         */
        public Builder withType(MessageType type) {
            this.type = type;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        protected WriteSupport<Group> getWriteSupport(Configuration conf) {
            return new KgReasonerGroupWriteSupport(type, extraMetaData);
        }

    }
}