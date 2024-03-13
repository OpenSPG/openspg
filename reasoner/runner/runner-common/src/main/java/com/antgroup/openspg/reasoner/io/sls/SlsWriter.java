/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io.sls;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.producer.LogProducer;
import com.aliyun.openservices.log.producer.ProducerConfig;
import com.aliyun.openservices.log.producer.ProjectConfig;
import com.antgroup.openspg.reasoner.common.table.Field;
import com.antgroup.openspg.reasoner.io.ITableWriter;
import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;
import com.antgroup.openspg.reasoner.io.model.SLSTableInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlsWriter implements ITableWriter {
  private int taskIndex;
  private SLSTableInfo slsTableInfo;
  private transient LogProducer logProducer;
  private String taskDevId;
  private List<String> outputColumnNameList;
  private List<LogItem> logItemList;
  private static final int cacheSize = 2000;
  private Long writeCount = 0L;

  @Override
  public void open(int taskIndex, int parallel, AbstractTableInfo tableInfo) {
    this.taskIndex = taskIndex;
    this.slsTableInfo = (SLSTableInfo) tableInfo;
    this.taskDevId = slsTableInfo.getTaskId();
    logItemList = new ArrayList<>(cacheSize);
    this.outputColumnNameList =
        slsTableInfo.getColumns().stream().map(Field::getName).collect(Collectors.toList());
    log.info("open SlsWriter,index=" + this.taskIndex + ",slsTableInfo=" + this.slsTableInfo);
    initLogProducer();
  }

  private void initLogProducer() {
    ProducerConfig producerConfig = new ProducerConfig();
    producerConfig.packageTimeoutInMS = 3000;
    LogProducer producer = new LogProducer(producerConfig);
    ProjectConfig projectConfig =
        new ProjectConfig(
            slsTableInfo.getProject(),
            slsTableInfo.getEndpoint(),
            slsTableInfo.getAccessId(),
            slsTableInfo.getAccessKey());
    producer.setProjectConfig(projectConfig);
    this.logProducer = producer;
  }

  @Override
  public void write(Object[] data) {
    writeCount++;
    Map<String, Object> content = new HashMap<>();
    for (int i = 0; i < data.length; i++) {
      content.put(outputColumnNameList.get(i), data[i]);
    }
    content.put("taskId", taskDevId);
    String dataStr = JSONObject.toJSONString(content);
    LogItem item = new LogItem();
    item.PushBack("content", dataStr);
    logItemList.add(item);
    if (writeCount % 1000 == 0) {
      log.info("write_sls_record content=" + dataStr + ", writer_count=" + writeCount);
    }
    if (logItemList.size() >= cacheSize) {
      flush();
    }
  }

  private void flush() {
    try {
      logProducer.send(
          slsTableInfo.getProject(),
          slsTableInfo.getLogStore(),
          slsTableInfo.getLogStore(),
          null,
          null,
          logItemList);
      logProducer.flush();
      logItemList.clear();
    } catch (Exception e) {
      throw new RuntimeException("SLS logProducer write error", e);
    }
  }

  @Override
  public void close() {
    if (null == logProducer) {
      return;
    }
    try {
      if (!logItemList.isEmpty()) {
        flush();
      }
      log.info(
          "sls_writer_close, index="
              + this.taskIndex
              + ", info="
              + slsTableInfo
              + ", odps_write_count="
              + writeCount);
      // Wait for the sending thread to end
      Thread.sleep(1000);
      logProducer.close();
    } catch (Exception e) {
      log.error("close_sls_writer_error", e);
      throw new RuntimeException(e);
    } finally {
      logProducer = null;
      writeCount = 0L;
      logItemList.clear();
    }
  }

  @Override
  public long writeCount() {
    return writeCount;
  }
}
