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

package com.antgroup.openspg.cloudext.impl.computing.local;

import com.antgroup.openspg.cloudext.impl.computing.local.impl.LocalBuilderExecutorImpl;
import com.antgroup.openspg.cloudext.impl.computing.local.impl.LocalReasonerExecutorImpl;
import com.antgroup.openspg.cloudext.interfaces.computing.ComputingClient;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.BuilderJobCanSubmitQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.BuilderJobProcessQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.BuilderJobSubmitCmd;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobCanSubmitQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobProcessQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobRunCmd;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobSubmitCmd;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerStatusWithProgress;
import com.antgroup.openspg.server.core.reasoner.model.service.TableReasonerReceipt;
import com.antgroup.openspg.server.common.model.datasource.connection.ComputingConnectionInfo;
import com.antgroup.openspg.server.core.builder.model.service.BuilderStatusWithProgress;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalComputingClient implements ComputingClient {

  private static final String BUILDER_NUMBER_OF_THREAD = "builder.nThreads";

  @Getter private final ComputingConnectionInfo connInfo;
  private final LocalBuilderExecutor localBuilderExecutor;
  private final LocalReasonerExecutor localReasonerExecutor;

  public LocalComputingClient(ComputingConnectionInfo connInfo) {
    this.connInfo = connInfo;

    localBuilderExecutor =
        new LocalBuilderExecutorImpl(
            (String) connInfo.getParamOrDefault(BUILDER_NUMBER_OF_THREAD, "*2"));
    localReasonerExecutor =
        new LocalReasonerExecutorImpl(
            (String) connInfo.getParamOrDefault(BUILDER_NUMBER_OF_THREAD, "*2"));
  }

  @Override
  public BuilderStatusWithProgress query(BuilderJobProcessQuery query) {
    return localBuilderExecutor.query(query);
  }

  @Override
  public boolean canSubmit(BuilderJobCanSubmitQuery query) {
    return localBuilderExecutor.canSubmit(query);
  }

  @Override
  public String submit(BuilderJobSubmitCmd cmd) {
    return localBuilderExecutor.submit(cmd);
  }

  @Override
  public ReasonerStatusWithProgress query(ReasonerJobProcessQuery query) {
    return localReasonerExecutor.query(query);
  }

  @Override
  public boolean canSubmit(ReasonerJobCanSubmitQuery query) {
    return localReasonerExecutor.canSubmit(query);
  }

  @Override
  public String submit(ReasonerJobSubmitCmd cmd) {
    return localReasonerExecutor.submit(cmd);
  }

  @Override
  public TableReasonerReceipt run(ReasonerJobRunCmd cmd) {
    return localReasonerExecutor.run(cmd);
  }
}
