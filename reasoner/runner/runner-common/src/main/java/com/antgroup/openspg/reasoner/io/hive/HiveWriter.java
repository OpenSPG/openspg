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

package com.antgroup.openspg.reasoner.io.hive;

import com.antgroup.openspg.reasoner.common.exception.HiveException;
import com.antgroup.openspg.reasoner.io.ITableWriter;
import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;
import com.antgroup.openspg.reasoner.io.model.HiveTableInfo;
import java.io.IOException;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.apache.parquet.schema.MessageType;

public class HiveWriter implements ITableWriter {
  private HiveTableInfo hiveTableInfo;

  private ParquetWriter<Group> writer;
  private SimpleGroupFactory groupFactory;

  private long writeCount = 0;

  /** open writer */
  @Override
  public void open(int taskIndex, int parallel, AbstractTableInfo tableInfo) {
    this.hiveTableInfo = (HiveTableInfo) tableInfo;
    HadoopOutputFile outputFile = HiveUtils.getHadoopOutputFile(hiveTableInfo, taskIndex);
    MessageType messageType = HiveUtils.createParquetMessageType(hiveTableInfo);
    try {
      this.writer =
          KgReasonerBaseParquetWriter.builder(outputFile)
              .withWriteMode(Mode.OVERWRITE)
              .withWriterVersion(ParquetProperties.WriterVersion.PARQUET_2_0)
              .withCompressionCodec(CompressionCodecName.UNCOMPRESSED)
              .withType(messageType)
              .build();
    } catch (IOException e) {
      throw new HiveException("build parquet writer error", e);
    }
    this.groupFactory = new SimpleGroupFactory(messageType);
  }

  /** write data */
  @Override
  public void write(Object[] data) {
    Group group = groupFactory.newGroup();
    HiveUtils.data2Group(data, group, hiveTableInfo);
    try {
      this.writer.write(group);
    } catch (IOException e) {
      throw new HiveException("write parquet error", e);
    }
    writeCount++;
  }

  @Override
  public void close() {
    if (null != writer) {
      try {
        this.writer.close();
      } catch (IOException e) {
        throw new HiveException("close parquet writer error", e);
      } finally {
        this.writer = null;
        this.writeCount = 0L;
      }
    }
  }

  @Override
  public long writeCount() {
    return this.writeCount;
  }
}
