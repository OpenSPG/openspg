/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io.sls;

import com.aliyun.openservices.log.producer.LogProducer;
import com.aliyun.openservices.log.producer.ProducerConfig;
import com.aliyun.openservices.log.producer.ProjectConfig;
import com.antgroup.openspg.reasoner.io.ITableWriter;
import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;
import com.antgroup.openspg.reasoner.io.model.SLSTableInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlsWriter implements ITableWriter {
    private int taskIndex;
    private SLSTableInfo slsTableInfo;
    private transient LogProducer logProducer;

    @Override
    public void open(int taskIndex, int parallel, AbstractTableInfo tableInfo) {
        this.taskIndex = taskIndex;
        this.slsTableInfo = (SLSTableInfo) tableInfo;
        log.info("open SlsWriter,index=" + this.taskIndex + ",slsTableInfo=" + this.slsTableInfo);
        initLogProducer();
    }

    private void initLogProducer() {
        this.taskId = config.get("taskId"); //重写taskId
        slsConfig.setOutputColumns(columns);

        ProducerConfig producerConfig = new ProducerConfig();
        producerConfig.packageTimeoutInMS = 3000;
        LogProducer producer = new LogProducer(producerConfig);
        ProjectConfig projectConfig = new ProjectConfig(slsTableInfo.getProject(),
                slsTableInfo.getEndpoint(), slsTableInfo.getAccessId(), slsTableInfo.getAccessKey());
        producer.setProjectConfig(projectConfig);
        this.logProducer = producer;

    }

    @Override
    public void write(Object[] data) {

    }

    @Override
    public void close() {

    }

    @Override
    public long writeCount() {
        return 0;
    }
}