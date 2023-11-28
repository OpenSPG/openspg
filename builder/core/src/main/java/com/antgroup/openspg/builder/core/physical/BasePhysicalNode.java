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

package com.antgroup.openspg.builder.core.physical;

import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Base class of physical execution nodes, initializes the physical execution node based on the
 * configuration of the logical execution node.
 *
 * <p>Currently, there are three types of physical nodes, namely data source nodes, processing
 * nodes, and sink nodes.
 *
 * <ul>
 *   <li>Data source nodes - Data source nodes are responsible for reading data from sources such as
 *       files, databases, graph stores, and so on.
 *   <li>Processing nodes - Process the data read from the data source, such as extracting
 *       structured data from unstructured text, standardizing non-standard field values, linking
 *       semantic type by semantic fields, and so on.
 *   <li>Sink Nodes - Sink nodes are used to output the build results to specific storage media,
 *       such as graph stores, search engines, and key-value stores.
 * </ul>
 */
@Getter
@AllArgsConstructor
public abstract class BasePhysicalNode implements Comparable<BasePhysicalNode> {

  /** ID of the physical node. */
  protected final String id;

  /** Name of the physical node. */
  protected final String name;

  /**
   * Runtime context of the physical node.
   *
   * <p>Provide runtime context information to each execution node, such as project ID, task ID,
   * instance ID, task parallelism, and so on. The task parallelism can be used for distributed data
   * reading and partitioning.
   *
   * <p>For detailed runtime parameters, please refer to the {@link RuntimeContext} class.
   */
  protected RuntimeContext context;

  /** Whether the node is initialized. */
  private volatile boolean isInitialized = false;

  public BasePhysicalNode(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public void init(RuntimeContext context) throws Exception {
    this.context = context;
    if (!isInitialized) {
      synchronized (this) {
        if (!isInitialized) {
          doInit(context);
          isInitialized = true;
        }
      }
    }
  }

  public void doInit(RuntimeContext context) throws Exception {}

  public abstract void close() throws Exception;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BasePhysicalNode)) {
      return false;
    }
    BasePhysicalNode that = (BasePhysicalNode) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public int compareTo(BasePhysicalNode o) {
    return o.getId().compareTo(this.id);
  }
}
