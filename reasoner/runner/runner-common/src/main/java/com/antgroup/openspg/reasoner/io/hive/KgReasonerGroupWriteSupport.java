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
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.GroupWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;

import java.util.HashMap;
import java.util.Map;


public class KgReasonerGroupWriteSupport extends WriteSupport<Group> {

    private final MessageType         schema;
    private final Map<String, String> extraMetaData;

    private GroupWriter groupWriter;

    public KgReasonerGroupWriteSupport() {
        this(null, new HashMap<String, String>());
    }

    public KgReasonerGroupWriteSupport(MessageType schema, Map<String, String> extraMetaData) {
        this.schema = schema;
        this.extraMetaData = extraMetaData;
    }

    @Override
    public String getName() {
        return "holmes";
    }

    @Override
    public WriteContext init(Configuration configuration) {
        return new WriteContext(schema, this.extraMetaData);
    }

    @Override
    public void prepareForWrite(RecordConsumer recordConsumer) {
        groupWriter = new GroupWriter(recordConsumer, schema);
    }

    @Override
    public void write(Group record) {
        groupWriter.write(record);
    }

}
