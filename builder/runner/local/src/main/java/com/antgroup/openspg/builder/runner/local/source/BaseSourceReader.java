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

package com.antgroup.openspg.builder.runner.local.source;

import com.antgroup.openspg.builder.core.physical.BasePhysicalNode;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.List;
import lombok.Getter;

/**
 * This is the base class for data source node.
 *
 * <p>It serves as the starting point of the entire pipeline and reads data through data source
 * configurations.
 *
 * <p>It provides the data to downstream nodes for further processing.
 *
 * <p>In tasks with multiple parallelism, it is important to consider the issue of parallel reading
 * of data through data partitioning.
 *
 * <p>Please check {@link BasePhysicalNode} class document for more details.
 */
@Getter
public abstract class BaseSourceReader<C> extends BasePhysicalNode {

  /**
   * The configuration of source reader node.
   *
   * <p>Each type of data source node configuration is different and needs to be implemented
   * independently. Taking CSV and database sources as examples.
   *
   * <ul>
   *   <li>CSV source config - Including the CSV file path, starting row number, and columns.
   *   <li>Database source config - Including the database name, table name, database username and
   *       password, and columns.
   * </ul>
   */
  protected final C config;

  public BaseSourceReader(String id, String name, C config) {
    super(id, name);
    this.config = config;
  }

  /**
   * Read data based on the data source configuration.
   *
   * @return Collection of data that has been read. The data source node returns the raw data
   *     primarily in the form of BuilderRecord, which is inherited from BaseRecord.
   */
  public abstract List<BaseRecord> read();
}
