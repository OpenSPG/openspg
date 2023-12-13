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

package com.antgroup.openspg.reasoner.task;

import com.antgroup.openspg.reasoner.graphstate.GraphStateTypeEnum;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import scala.Tuple2;

@Data
@Builder
public class TaskRecord implements Serializable {
  /** task id */
  private String taskId;

  /** task parallel */
  private int parallel;

  /** graph state type */
  private GraphStateTypeEnum graphStateType;

  /** class name of graph loader */
  private String graphLoaderJobClassName;

  /** start id from input */
  private List<Tuple2<String, String>> startIdList;

  /** expect batch number, batching control */
  private int expectBatchNum;

  /** dsl */
  private String dsl;

  /** initializer class list */
  private List<String> initializerClassList;

  /** task params */
  private Map<String, Object> params;
}
