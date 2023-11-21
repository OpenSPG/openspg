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

package com.antgroup.openspg.core.spgbuilder.service.impl;

import com.antgroup.openspg.api.facade.client.SchemaFacade;
import com.antgroup.openspg.api.facade.dto.builder.request.BuilderJobInstQuery;
import com.antgroup.openspg.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.api.http.client.HttpSchemaFacade;
import com.antgroup.openspg.cloudext.interfaces.computing.ComputingClient;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.BuilderJobCanSubmitQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.BuilderJobProcessQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.BuilderJobSubmitCmd;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.JobSchedulerClient;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.common.model.datasource.DataSource;
import com.antgroup.openspg.common.model.datasource.DataSourceUsageTypeEnum;
import com.antgroup.openspg.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.common.model.datasource.connection.SearchEngineConnectionInfo;
import com.antgroup.openspg.common.model.datasource.connection.TableStoreConnectionInfo;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.common.service.config.AppEnvConfig;
import com.antgroup.openspg.common.service.datasource.DataSourceService;
import com.antgroup.openspg.core.spgbuilder.engine.physical.invoker.operator.impl.PythonOperatorFactory;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.Node;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.NodeTypeEnum;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.Pipeline;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.GraphStoreSinkNodeConfig;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInfo;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInst;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderStatusWithProgress;
import com.antgroup.openspg.core.spgbuilder.model.service.FailureBuilderResult;
import com.antgroup.openspg.core.spgbuilder.service.BuilderJobInfoService;
import com.antgroup.openspg.core.spgbuilder.service.BuilderJobInstService;
import com.antgroup.openspg.core.spgbuilder.service.repo.BuilderJobInstRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuilderJobInstServiceImpl implements BuilderJobInstService {

  @Autowired private AppEnvConfig appEnvConfig;

  @Autowired private BuilderJobInstRepository builderJobInstRepository;

  @Autowired private DataSourceService dataSourceService;

  @Autowired private BuilderJobInfoService builderJobInfoService;

  private final SchemaFacade schemaFacade = new HttpSchemaFacade();

  @Override
  public Long create(BuilderJobInfo builderJobInfo, BuilderJobInst builderJobInst) {
    JobSchedulerClient jobSchedulerClient = dataSourceService.buildSharedJobSchedulerClient();
    Long buildingJobInstId = builderJobInstRepository.save(builderJobInst);

    SchedulerJobInst schedulerJobInst =
        new SchedulerJobInst(
            null,
            builderJobInfo.getExternalJobInfoId(),
            JobTypeEnum.BUILDING.name(),
            builderJobInst.getStatus(),
            null,
            String.valueOf(buildingJobInstId));
    String schedulerJobInstId = jobSchedulerClient.createJobInst(schedulerJobInst);

    builderJobInst.setExternalJobInstId(schedulerJobInstId);
    builderJobInstRepository.updateExternalJobId(buildingJobInstId, schedulerJobInstId);
    return buildingJobInstId;
  }

  @Override
  public List<BuilderJobInst> query(BuilderJobInstQuery query) {
    return builderJobInstRepository.query(query);
  }

  @Override
  @Transactional
  public BuilderJobInst pollingBuilderJob(SchedulerJobInst jobInst) {
    ComputingClient computingClient = dataSourceService.buildSharedComputingClient();

    BuilderJobInst builderJobInst = queryByExternalJobInstId(jobInst.getJobInstId());
    if (builderJobInst.isFinished()) {
      return builderJobInst;
    } else if (builderJobInst.isRunning()) {
      BuilderStatusWithProgress progress =
          computingClient.query(
              new BuilderJobProcessQuery(String.valueOf(builderJobInst.getJobInstId())));
      if (progress == null) {
        // if status is null, rerun it
        progress = new BuilderStatusWithProgress(JobInstStatusEnum.QUEUE);
        builderJobInst.setProgress(progress);
        builderJobInstRepository.queue(builderJobInst.getJobInstId(), progress);
        return builderJobInst;
      } else if (progress.getStatus().isRunning()) {
        builderJobInstRepository.running(builderJobInst.getJobInstId(), progress);
        return builderJobInst;
      } else if (progress.getStatus().isFinished()) {
        builderJobInst.setProgress(progress);
        builderJobInstRepository.finish(builderJobInst.getJobInstId(), progress);
        return builderJobInst;
      }
    }

    // The task is not in finished or running status, try to submit the task
    if (!computingClient.canSubmit(new BuilderJobCanSubmitQuery())) {
      return builderJobInst;
    }

    BuilderJobInfo builderJobInfo = builderJobInfoService.queryById(builderJobInst.getJobId());
    GraphStoreSinkNodeConfig sinkNodeConfig = fillSinkNodeConfig(builderJobInfo);
    String submit =
        computingClient.submit(
            new BuilderJobSubmitCmd(
                builderJobInst,
                builderJobInfo,
                sinkNodeConfig,
                appEnvConfig.getSchemaUri(),
                schemaFacade
                    .queryProjectSchema(new ProjectSchemaRequest(builderJobInfo.getProjectId()))
                    .getData(),
                builderParams()));
    if (submit != null) {
      // The task is submitted successfully and the task is set to running status.
      BuilderStatusWithProgress progress =
          new BuilderStatusWithProgress(JobInstStatusEnum.RUNNING, null, null);
      builderJobInstRepository.start(builderJobInst.getJobInstId(), progress);
      return builderJobInst;
    }
    return builderJobInst;
  }

  @Override
  public BuilderJobInst queryByExternalJobInstId(String externalJobInstId) {
    BuilderJobInstQuery query = new BuilderJobInstQuery().setExternalJobInstId(externalJobInstId);
    List<BuilderJobInst> jobInsts = builderJobInstRepository.query(query);
    return CollectionUtils.isNotEmpty(jobInsts) ? jobInsts.get(0) : null;
  }

  @Override
  public int updateToFailure(Long jobInstId, FailureBuilderResult result) {
    return builderJobInstRepository.finish(
        jobInstId, new BuilderStatusWithProgress(JobInstStatusEnum.FAILURE, result, null));
  }

  private GraphStoreSinkNodeConfig fillSinkNodeConfig(BuilderJobInfo jobInfo) {
    Pipeline pipeline = jobInfo.getPipeline();
    for (Node node : pipeline.getNodes()) {
      if (NodeTypeEnum.GRAPH_SINK.equals(node.getType())) {
        GraphStoreSinkNodeConfig nodeConfig = (GraphStoreSinkNodeConfig) node.getNodeConfig();
        if (nodeConfig.getGraphStoreConnectionInfo() == null) {
          DataSource graphStoreDataSource =
              dataSourceService.getFirstDataSource(
                  jobInfo.getProjectId(), DataSourceUsageTypeEnum.KG_STORE);
          nodeConfig.setGraphStoreConnectionInfo(
              (GraphStoreConnectionInfo) graphStoreDataSource.getConnectionInfo());
        }
        if (nodeConfig.getSearchEngineConnectionInfo() == null) {
          DataSource searchEngineDataSource =
              dataSourceService.getFirstDataSource(
                  jobInfo.getProjectId(), DataSourceUsageTypeEnum.SEARCH);
          nodeConfig.setSearchEngineConnectionInfo(
              (SearchEngineConnectionInfo) searchEngineDataSource.getConnectionInfo());
        }
        if (nodeConfig.getTableStoreConnectionInfo() == null) {
          DataSource tableStoreDataSource =
              dataSourceService.getFirstDataSource(
                  jobInfo.getProjectId(), DataSourceUsageTypeEnum.TABLE_STORE);
          nodeConfig.setTableStoreConnectionInfo(
              (TableStoreConnectionInfo) tableStoreDataSource.getConnectionInfo());
        }
        return nodeConfig;
      }
    }
    throw new IllegalStateException("graph sink node is null");
  }

  private Map<String, Object> builderParams() {
    Map<String, Object> params = new HashMap<>(5);
    params.put(BuilderJobInfo.SEARCH_ENGINE, appEnvConfig.getEnableSearchEngine());
    params.put(PythonOperatorFactory.PYTHON_EXEC, appEnvConfig.getBuilderOperatorPythonExec());
    params.put(PythonOperatorFactory.PYTHON_PATHS, appEnvConfig.getBuilderOperatorPythonPaths());
    return params;
  }
}
