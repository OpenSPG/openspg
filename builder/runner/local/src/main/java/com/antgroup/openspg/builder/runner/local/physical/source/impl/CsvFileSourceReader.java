package com.antgroup.openspg.builder.runner.local.physical.source.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.CsvSourceNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.builder.runner.local.physical.source.BaseSourceReader;
import com.antgroup.openspg.common.util.StringUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvFileSourceReader extends BaseSourceReader<CsvSourceNodeConfig> {

  private Queue<BaseRecord> queue;
  private CSVReader csvReader;
  private Iterator<String[]> lines;
  private AtomicLong lineNumber;

  public CsvFileSourceReader(String id, String name, CsvSourceNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    queue = new ArrayBlockingQueue<>(context.getBatchSize() * context.getParallelism());
    try {
      csvReader =
          new CSVReaderBuilder(new FileReader(config.getUrl()))
              .withSkipLines(config.getStartRow() - 1)
              .build();
    } catch (FileNotFoundException e) {
      throw new BuilderException(e, "csv file={} is not exist.", config.getUrl());
    }
    lineNumber = new AtomicLong(config.getStartRow() - 1);
    lines = csvReader.iterator();
  }

  @Override
  public void close() throws Exception {
    if (csvReader != null) {
      csvReader.close();
    }
  }

  @Override
  public List<BaseRecord> read() {
    putQueue();
    List<BaseRecord> results = new ArrayList<>(context.getBatchSize());
    for (int i = 0; i < context.getBatchSize(); i++) {
      BaseRecord poll = queue.poll();
      if (poll == null) {
        putQueue();
        poll = queue.poll();
      }

      // 如果拿到的是空，则说明队列中确实没有了，则breaks
      if (poll == null) {
        break;
      }
      results.add(poll);
    }
    return results;
  }

  private void putQueue() {
    if (queue.size() < context.getBatchSize()) {
      synchronized (this) {
        if (queue.size() < context.getBatchSize()) {
          int curLen = queue.size();
          int batchSize = context.getBatchSize() * context.getParallelism();
          while (curLen < batchSize && lines.hasNext()) {
            String[] next = lines.next();
            lineNumber.addAndGet(1);
            if (next == null || (next.length == 1 && StringUtils.isBlank(next[0]))) {
              continue;
            }
            queue.add(parse(next));
            curLen += 1;
          }
        }
      }
    }
  }

  private BuilderRecord parse(String[] fields) {
    Map<String, String> props = new HashMap<>(config.getColumns().size());
    for (int i = 0; i < config.getColumns().size(); i++) {
      String column = config.getColumns().get(i);
      if (i >= fields.length) {
        props.put(column, null);
      } else {
        props.put(column, fields[i]);
      }
    }
    return new BuilderRecord("line" + lineNumber.get(), null, props);
  }
}
