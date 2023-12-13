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

package com.antgroup.openspg.reasoner.sink;

import com.antgroup.openspg.reasoner.common.Utils;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "userlogger")
public class KgReasonerCsvWriter {

  private static final Map<String, CSVWriter> CSV_WRITER_MAP = new ConcurrentHashMap<>();

  public static void write(String file, Object[] data) {
    // log.info("KgReasonerCsvWriter,file=" + file + ",data=" + Arrays.toString(data));
    CSVWriter writer =
        CSV_WRITER_MAP.computeIfAbsent(
            file,
            k -> {
              Utils.deletePath(file);
              Utils.createFile(file);
              try {
                return new CSVWriter(new FileWriter(file));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
    String[] strData = new String[data.length];
    for (int i = 0; i < data.length; ++i) {
      strData[i] = String.valueOf(data[i]);
    }

    List<String[]> listStrData = new ArrayList<>();
    listStrData.add(strData);
    writer.writeAll(listStrData);
  }

  public static void flush(String file) {
    CSVWriter writer = CSV_WRITER_MAP.remove(file);
    if (null == writer) {
      return;
    }
    try {
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
