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

package com.antgroup.openspg.reasoner.local.model;

import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.local.impl.LocalReasonerSession;
import com.antgroup.openspg.reasoner.local.rdg.LocalRDG;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.physical.operators.PhysicalOperator;
import com.antgroup.openspg.reasoner.recorder.IExecutionRecorder;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.Data;
import scala.Tuple2;

@Data
public class LocalReasonerTask implements Serializable {
  /** task id */
  private static final long serialVersionUID = 8591924774057455987L;

  private String id = "";
  /** Choose between dsl or dslDagList */
  private String dsl = null;

  private List<PhysicalOperator<LocalRDG>> dslDagList = null;
  private LocalReasonerSession session = null;

  /** pass catalog to runner or provide schema connection info */
  private Catalog catalog = null;

  private KgSchemaConnectionInfo connInfo = null;

  /** Choose between graphLoadClass or graphState */
  private String graphLoadClass = null;

  private GraphState<IVertexId> graphState = null;

  /** start id from input */
  private List<Tuple2<String, String>> startIdList;

  /**
   * parameters ConfigKey.KG_REASONER_MAX_PATH_LIMIT, set max path, exceeding the threshold without
   * reporting an error, will truncate path data
   * ConfigKey.KG_REASONER_EXCEEDING_PATH_THRESHOLD_ERROR, set strict max path limit, exceeding the
   * threshold will throw an exception
   */
  private Map<String, Object> params = new HashMap<>();

  /**
   * thread pool can be null, will use default threadpool
   * [com.antgroup.openspg.reasoner.local.impl.LocalRunnerThreadPool]
   */
  private ThreadPoolExecutor threadPoolExecutor = null;

  /** thread pool executor timeout ms */
  private long executorTimeoutMs = 3 * 1000;

  /** execution information recorder, for debug */
  private IExecutionRecorder executionRecorder = null;
}
