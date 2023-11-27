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

package com.antgroup.openspg.server.core.builder.model.pipeline.config;

import com.antgroup.openspg.core.spgbuilder.model.pipeline.NodeTypeEnum;
import java.util.List;

public class CsvSourceNodeConfig extends BaseNodeConfig {

  /** start row */
  private final int startRow;

  /** fileUrl */
  private final String url;

  /** columns */
  private final List<String> columns;

  public CsvSourceNodeConfig(String url, int startRow, List<String> columns) {
    super(NodeTypeEnum.CSV_SOURCE);
    this.url = url;
    this.startRow = startRow;
    this.columns = columns;
  }

  public int getStartRow() {
    return startRow;
  }

  public String getUrl() {
    return url;
  }

  public List<String> getColumns() {
    return columns;
  }
}
