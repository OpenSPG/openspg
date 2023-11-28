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

import com.antgroup.kg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.kg.reasoner.local.KGReasonerLocalRunner;
import com.antgroup.kg.reasoner.local.model.LocalReasonerResult;
import com.antgroup.kg.reasoner.local.model.LocalReasonerTask;
import com.antgroup.openspg.cloudext.impl.computing.local.LocalReasonerExecutor;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobCanSubmitQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobProcessQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobRunCmd;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobSubmitCmd;
import com.antgroup.openspg.cloudext.interfaces.tablestore.TableFileHandler;
import com.antgroup.openspg.cloudext.interfaces.tablestore.TableStoreClient;
import com.antgroup.openspg.cloudext.interfaces.tablestore.TableStoreClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.tablestore.cmd.TableFileCreateCmd;
import com.antgroup.openspg.cloudext.interfaces.tablestore.model.ColumnMeta;
import com.antgroup.openspg.cloudext.interfaces.tablestore.model.TableRecord;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.thread.ThreadUtils;
import com.antgroup.openspg.server.core.reasoner.model.ReasonerException;
import com.antgroup.openspg.server.core.reasoner.model.service.BaseReasonerContent;
import com.antgroup.openspg.server.core.reasoner.model.service.BaseReasonerResult;
import com.antgroup.openspg.server.core.reasoner.model.service.FailureReasonerResult;
import com.antgroup.openspg.server.core.reasoner.model.service.KgdslReasonerContent;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInfo;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInst;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerProgress;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerStatusWithProgress;
import com.antgroup.openspg.server.core.reasoner.model.service.SuccessReasonerResult;
import com.antgroup.openspg.server.core.reasoner.model.service.TableReasonerReceipt;
import com.antgroup.openspg.core.spgreasoner.service.util.LocalRunnerUtils;
import com.antgroup.openspg.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
public class LocalReasonerExecutorImpl implements LocalReasonerExecutor {

  // for reasoner job
  private final ThreadPoolExecutor jobDriver;
  private final ThreadPoolExecutor jobWorker;
  private final Map<String, Future<ReasonerStatusWithProgress>> runningReasonerJobInst;

  // for olap
  private final ThreadPoolExecutor olapDriver;
  private final ThreadPoolExecutor olapWorker;

  public LocalReasonerExecutorImpl(String nThreads) {
    jobDriver = ThreadUtils.newDaemonFixedThreadPool(1, "localReasonerDriver");
    jobWorker =
        ThreadUtils.newDaemonFixedThreadPool(ThreadUtils.nThreads(nThreads), "localReasonerWorker");

    olapDriver =
        ThreadUtils.newDaemonFixedThreadPool(ThreadUtils.nThreads("*1"), "olapReasonerDriver");
    olapWorker =
        ThreadUtils.newDaemonFixedThreadPool(ThreadUtils.nThreads(nThreads), "olapReasonerWorker");
    runningReasonerJobInst = new ConcurrentHashMap<>();
  }

  @Override
  public ReasonerStatusWithProgress query(ReasonerJobProcessQuery query) {
    Future<ReasonerStatusWithProgress> future =
        runningReasonerJobInst.get(query.getComputingJobInstId());
    if (future == null) {
      return null;
    }
    if (future.isDone()) {
      // if finished, remove it from runningBuilderJobInst
      runningReasonerJobInst.remove(query.getComputingJobInstId());
      ReasonerStatusWithProgress progress = null;
      try {
        progress = future.get();
      } catch (Throwable e) {
        throw ReasonerException.reasonerError(e);
      }
      return progress;
    } else {
      return new ReasonerStatusWithProgress(JobInstStatusEnum.RUNNING);
    }
  }

  @Override
  public boolean canSubmit(ReasonerJobCanSubmitQuery query) {
    return runningReasonerJobInst.isEmpty();
  }

  @Override
  public String submit(ReasonerJobSubmitCmd cmd) {
    ReasonerJobInst jobInst = cmd.getJobInst();
    if (runningReasonerJobInst.isEmpty()) {
      synchronized (runningReasonerJobInst) {
        if (runningReasonerJobInst.isEmpty()) {
          Future<ReasonerStatusWithProgress> future = doSubmit(cmd);
          String computingJobInstId = String.valueOf(jobInst.getJobInstId());
          runningReasonerJobInst.put(computingJobInstId, future);
          return computingJobInstId;
        }
      }
    }
    return null;
  }

  @Override
  public TableReasonerReceipt run(ReasonerJobRunCmd cmd) {
    Future<TableReasonerReceipt> future =
        olapDriver.submit(
            () -> {
              LocalReasonerResult localReasonerResult =
                  doRun(
                      cmd.getProjectId(),
                      cmd.getSchemaUrl(),
                      cmd.getConnInfo(),
                      cmd.getContent(),
                      olapWorker);
              return buildReceipt(localReasonerResult);
            });
    try {
      return future.get(3, TimeUnit.MINUTES);
    } catch (InterruptedException | ExecutionException e) {
      throw ReasonerException.reasonerError(e);
    } catch (TimeoutException e) {
      future.cancel(true);
      throw ReasonerException.timeout(3);
    }
  }

  private Future<ReasonerStatusWithProgress> doSubmit(ReasonerJobSubmitCmd cmd) {
    return jobDriver.submit(
        () -> {
          try {
            ReasonerJobInfo jobInfo = cmd.getJobInfo();
            LocalReasonerResult localReasonerResult =
                doRun(
                    jobInfo.getProjectId(),
                    cmd.getSchemaUrl(),
                    cmd.getGraphStoreConnInfo(),
                    jobInfo.getContent(),
                    jobWorker);
            JobInstStatusEnum status = null;
            BaseReasonerResult result = null;
            ReasonerProgress progress = new ReasonerProgress();
            if (StringUtils.isNotBlank(localReasonerResult.getErrMsg())) {
              status = JobInstStatusEnum.FAILURE;
              result = new FailureReasonerResult(localReasonerResult.getErrMsg());
            } else {
              status = JobInstStatusEnum.SUCCESS;
              result = writeResult2TableFile(cmd, localReasonerResult);
            }
            return new ReasonerStatusWithProgress(status, result, progress);
          } catch (Throwable e) {
            throw ReasonerException.reasonerError(e);
          }
        });
  }

  private LocalReasonerResult doRun(
      Long projectId,
      String schemaUrl,
      GraphStoreConnectionInfo connInfo,
      BaseReasonerContent content,
      ThreadPoolExecutor executor) {
    KGReasonerLocalRunner localRunner = new KGReasonerLocalRunner();
    LocalReasonerTask reasonerTask = new LocalReasonerTask();
    reasonerTask.setThreadPoolExecutor(executor);
    reasonerTask.setCatalog(
        LocalRunnerUtils.buildCatalog(projectId, new KgSchemaConnectionInfo(schemaUrl, "")));
    reasonerTask.setGraphState(LocalRunnerUtils.buildGraphState(connInfo));
    reasonerTask.setDsl(((KgdslReasonerContent) content).getKgdsl());
    return localRunner.run(reasonerTask);
  }

  private SuccessReasonerResult writeResult2TableFile(
      ReasonerJobSubmitCmd cmd, LocalReasonerResult result) throws Exception {
    TableStoreClient tableStoreClient =
        TableStoreClientDriverManager.getClient(cmd.getTableStoreConnInfo());

    if (CollectionUtils.isEmpty(result.getColumns())) {
      return new SuccessReasonerResult(null);
    }
    String tableName = cmd.tableName();
    TableFileHandler fileHandler =
        tableStoreClient.create(
            new TableFileCreateCmd(
                tableName,
                result.getColumns().stream().map(ColumnMeta::new).toArray(ColumnMeta[]::new)));

    fileHandler.batchWrite(
        result.getRows().stream().map(TableRecord::new).collect(Collectors.toList()));
    fileHandler.close();
    return new SuccessReasonerResult(fileHandler.getTableName());
  }

  private TableReasonerReceipt buildReceipt(LocalReasonerResult reasonerResult) {
    if (StringUtils.isNotBlank(reasonerResult.getErrMsg())) {
      throw ReasonerException.reasonerError(reasonerResult.getErrMsg());
    }
    return new TableReasonerReceipt(
        reasonerResult.getColumns(),
        reasonerResult.getRows().stream().map(Arrays::asList).collect(Collectors.toList()));
  }
}
