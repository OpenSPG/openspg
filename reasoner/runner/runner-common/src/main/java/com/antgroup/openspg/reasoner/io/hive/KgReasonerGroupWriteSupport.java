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

/**
 * @author donghai.ydh
 * @version HolmesHolmesGroupWriteSupport.java, v 0.1 2022年11月22日 14:16 donghai.ydh
 */
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
