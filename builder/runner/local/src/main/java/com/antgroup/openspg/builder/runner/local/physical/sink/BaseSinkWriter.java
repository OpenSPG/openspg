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

package com.antgroup.openspg.builder.runner.local.physical.sink;

import com.antgroup.openspg.builder.core.physical.BasePhysicalNode;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.List;
import lombok.Getter;

/**
 * Base class of Sink nodes, outputs the execution results of the physical execution plan to the
 * specified storage, such as graph storage, search engines, key-value stores, and so on.
 *
 * <p>This class inherits from {@link BasePhysicalNode}, but it also has its own writer method to
 * implement data output.
 */
@Getter
public abstract class BaseSinkWriter<C> extends BasePhysicalNode {

  /**
   * The configuration of sink writer node.
   *
   * <p>Taking TuGraph and Elasticsearch as examples.
   *
   * <ul>
   *   <li>TuGraph - Including schema("tugraph"), graphName("default"), host("127.0.0.1:9090"),
   *       accessId, and accessKey.
   *   <li>Elasticsearch - Including schema("elasticsearch"), host("127.0.0.1"), scheme("http"), and
   *       port("9200").
   * </ul>
   */
  protected final C config;

  public BaseSinkWriter(String id, String name, C config) {
    super(id, name);
    this.config = config;
  }

  /**
   * Outputting the execution results of the physical execution plan to the specified storage.
   *
   * @param records: Data produced by upstream processing nodes.
   */
  public abstract void write(List<BaseRecord> records);
}
