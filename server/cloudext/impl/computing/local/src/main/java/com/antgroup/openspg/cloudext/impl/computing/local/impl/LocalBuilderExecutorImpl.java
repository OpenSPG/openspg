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

package com.antgroup.openspg.cloudext.impl.computing.local.impl;

import static com.antgroup.openspg.builder.core.runtime.RuntimeContext.GRAPH_STORE_CONN_INFO;
import static com.antgroup.openspg.builder.core.runtime.RuntimeContext.SEARCH_ENGINE_CONN_INFO;
import static com.antgroup.openspg.builder.core.runtime.RuntimeContext.TABLE_STORE_CONN_INFO;

import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
import com.antgroup.openspg.builder.core.runtime.BuilderMetric;
import com.antgroup.openspg.builder.core.runtime.BuilderStat;
import com.antgroup.openspg.builder.core.runtime.PipelineExecutor;
import com.antgroup.openspg.builder.core.runtime.RecordCollector;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.core.runtime.impl.DefaultPipelineExecutor;
import com.antgroup.openspg.builder.core.runtime.impl.DefaultRecordCollector;
import com.antgroup.openspg.cloudext.impl.computing.local.LocalBuilderExecutor;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.BuilderJobCanSubmitQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.BuilderJobProcessQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.BuilderJobSubmitCmd;
import com.antgroup.openspg.server.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.common.util.thread.ThreadUtils;
import com.antgroup.openspg.server.core.builder.model.service.BaseBuilderResult;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInfo;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInst;
import com.antgroup.openspg.server.core.builder.model.service.BuilderProgress;
import com.antgroup.openspg.server.core.builder.model.service.BuilderStatusWithProgress;
import com.antgroup.openspg.server.core.builder.model.service.SuccessBuilderResult;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalBuilderExecutorImpl implements LocalBuilderExecutor {

  private final ThreadPoolExecutor executor;
  private final PipelineExecutor pipelineExecutor;
  private final Map<String, BuilderStat> runningBuilderJobInst;

  public LocalBuilderExecutorImpl(String nThreads) {
    executor =
        ThreadUtils.newDaemonFixedThreadPool(
            ThreadUtils.nThreads(nThreads), "localBuilderExecutor");
    pipelineExecutor = new DefaultPipelineExecutor();
    runningBuilderJobInst = new ConcurrentHashMap<>();
  }

  @Override
  public BuilderStatusWithProgress query(BuilderJobProcessQuery query) {
    BuilderStat builderStat = runningBuilderJobInst.get(query.getComputingJobInstId());
    if (builderStat == null) {
      return null;
    }

    JobInstStatusEnum status = null;
    BaseBuilderResult result = null;
    BuilderProgress progress =
        new BuilderProgress(
            builderStat.getMetric().getTotalCnt().getCount(),
            builderStat.getMetric().getErrorCnt().getCount());
    if (builderStat.isFinished()) {
      // if finished, remove it from runningBuilderJobInst
      runningBuilderJobInst.remove(query.getComputingJobInstId());
      if (builderStat.isFailure()) {
        status = JobInstStatusEnum.FAILURE;
      } else {
        String errorTableFile = null;
        RecordCollector collector = builderStat.getCollector();
        if (collector.haveCollected()) {
          errorTableFile = collector.getTableName();
        }
        result =
            new SuccessBuilderResult(
                progress.getProcessedCnt(), progress.getErrorCnt(), errorTableFile);
        status = JobInstStatusEnum.SUCCESS;
      }
    } else {
      status = JobInstStatusEnum.RUNNING;
    }
    return new BuilderStatusWithProgress(status, result, progress);
  }

  @Override
  public boolean canSubmit(BuilderJobCanSubmitQuery query) {
    return runningBuilderJobInst.isEmpty();
  }

  @Override
  public String submit(BuilderJobSubmitCmd cmd) {
    BuilderJobInst jobInst = cmd.getJobInst();
    if (runningBuilderJobInst.isEmpty()) {
      synchronized (runningBuilderJobInst) {
        if (runningBuilderJobInst.isEmpty()) {
          BuilderStat builderStat = doSubmit(cmd);
          String computingJobInstId = String.valueOf(jobInst.getJobInstId());
          runningBuilderJobInst.put(computingJobInstId, builderStat);
          return computingJobInstId;
        }
      }
    }
    return null;
  }

  private BuilderStat doSubmit(BuilderJobSubmitCmd cmd) {
    BuilderJobInfo jobInfo = cmd.getJobInfo();
    BuilderJobInst jobInst = cmd.getJobInst();
    PhysicalPlan physicalPlan = pipelineExecutor.plan(jobInfo.getPipeline());

    int parallelism = jobInfo.getParallelism();

    RecordCollector recordCollector = buildRecordCollector(cmd);

    BuilderMetric builderMetric = new BuilderMetric(jobInfo.getJobName());
    builderMetric.reportToLog();
    BuilderStat builderStat = new BuilderStat(builderMetric, recordCollector, parallelism);

    Map<String, Object> params = buildParams(cmd);
    for (int i = 0; i < parallelism; i++) {
      RuntimeContext runtimeContext =
          new RuntimeContext(
              jobInfo.getProjectId(),
              cmd.getSchemaUrl(),
              jobInfo.getJobName(),
              jobInst.getJobInstId(),
              jobInfo.getOperationType(),
              i,
              parallelism,
              jobInfo.getBatchSize(),
              cmd.getProjectSchema(),
              params,
              builderMetric,
              recordCollector);
      int finalI = i;
      executor.submit(
          () -> {
            try {
              pipelineExecutor.execute(physicalPlan, runtimeContext);
              builderStat.success(finalI);
            } catch (Throwable e) {
              builderStat.failure(finalI);
              log.error("run buildingJobInstId={} error", jobInst.getJobInstId(), e);
            } finally {
              if (builderStat.isFinished()) {
                try {
                  builderMetric.close();
                  recordCollector.close();
                } catch (Throwable e) {
                  log.error("close builderMetric or recordCollector error", e);
                }
              }
            }
          });
    }
    return builderStat;
  }

  public Map<String, Object> buildParams(BuilderJobSubmitCmd cmd) {
    Map<String, Object> params = new HashMap<>(10);
    params.put(GRAPH_STORE_CONN_INFO, cmd.getSinkNodeConfig().getGraphStoreConnectionInfo());
    params.put(SEARCH_ENGINE_CONN_INFO, cmd.getSinkNodeConfig().getSearchEngineConnectionInfo());
    params.put(TABLE_STORE_CONN_INFO, cmd.getSinkNodeConfig().getTableStoreConnectionInfo());
    params.putAll(cmd.getJobInfo().getParams());
    params.putAll(cmd.getParams());
    return Collections.unmodifiableMap(params);
  }

  public RecordCollector buildRecordCollector(BuilderJobSubmitCmd cmd) {
    return new DefaultRecordCollector(
        String.format(
            "spgbuilder_%s_%s_errorRecord",
            cmd.getJobInfo().getJobName(), cmd.getJobInst().getJobInstId()),
        cmd.getSinkNodeConfig().getTableStoreConnectionInfo());
  }
}
