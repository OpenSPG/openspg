/*
 * Copyright 2023 OpenSPG Authors
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
package com.antgroup.openspg.server.core.scheduler.service.task.sync.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.datasource.meta.DataSourceMeta;
import com.antgroup.openspg.server.common.service.datasource.meta.client.CloudDataSource;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.config.SchedulerConfig;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.task.sync.SyncTaskExecuteTemplate;
import com.antgroup.openspg.server.core.scheduler.service.utils.SchedulerUtils;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("checkPartitionSyncTask")
public class CheckPartitionSyncTask extends SyncTaskExecuteTemplate {

  private static final long SINGLE_INSTANCE_IMPORT_LIMIT = 100L * 10000L * 10000L;

  @Autowired private SchedulerConfig schedulerConfig;

  @Autowired private BuilderJobService builderJobService;

  @Autowired private SchedulerInstanceService instanceService;

  @Autowired private DataSourceMeta dataSourceMeta;

  @Override
  public SchedulerEnum.TaskStatus submit(TaskExecuteContext context) {
    SchedulerInstance instance = context.getInstance();

    long days =
        TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - instance.getGmtCreate().getTime());
    Integer lastDays = schedulerConfig.getExecuteMaxDay();
    if (days > (lastDays / 2)) {
      context.addTraceLog(
          "The partition has not existed for more than %s days. If no partition is created after %s days, the task will not be scheduled",
          days, lastDays);
    }
    if (SchedulerEnum.Dependence.DEPENDENT.equals(instance.getDependence())) {
      return processByDependent(context);
    } else {
      return processByIndependent(context);
    }
  }

  public SchedulerEnum.TaskStatus processByIndependent(TaskExecuteContext context) {
    context.addTraceLog("Check the partition is generated");
    return checkPartitionExist(context);
  }

  public SchedulerEnum.TaskStatus processByDependent(TaskExecuteContext context) {
    context.addTraceLog("Check the predecessor task is completed");

    SchedulerInstance instance = context.getInstance();
    SchedulerJob job = context.getJob();
    String cron = job.getSchedulerCron();
    Date prevDate = CommonUtils.getPreviousValidTime(cron, instance.getSchedulerDate());

    String uniqueId = SchedulerUtils.getUniqueId(job.getId(), prevDate);
    SchedulerInstance prevJobInstance = instanceService.getByUniqueId(uniqueId);

    if (null != prevJobInstance) {
      if (SchedulerEnum.InstanceStatus.isFinished(prevJobInstance.getStatus())) {
        return checkPartitionExist(context);
      } else {
        context.addTraceLog(
            "Predecessor instance:%s(%s) has not been completed",
            prevJobInstance.getId(), prevJobInstance.getUniqueId());
        return SchedulerEnum.TaskStatus.RUNNING;
      }
    } else {
      return checkPartitionExist(context);
    }
  }

  public SchedulerEnum.TaskStatus checkPartitionExist(TaskExecuteContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerJob job = context.getJob();
    BuilderJob builderJob = builderJobService.getById(Long.valueOf(job.getInvokerId()));

    boolean hasPartition = false;

    JSONObject extension = JSON.parseObject(builderJob.getExtension());
    JSONObject dataSourceConfig = extension.getJSONObject(BuilderConstant.DATASOURCE_CONFIG);
    DataSource dataSource =
        JSON.parseObject(dataSourceConfig.getString(BuilderConstant.DATASOURCE), DataSource.class);
    String project = dataSourceConfig.getString(BuilderConstant.DATABASE);
    String table = dataSourceConfig.getString(BuilderConstant.TABLE);
    String partition = dataSourceConfig.getString(BuilderConstant.PARTITION);

    CloudDataSource source = CloudDataSource.toCloud(dataSource);
    String dataSourceId = project + "." + table;

    String format = CommonUtils.getDateFormatByPartition(partition);
    String bizDate = DateTimeUtils.getDate2Str(format, instance.getSchedulerDate());
    String schedulerPartition =
        CommonUtils.replacePartition(partition, instance.getSchedulerDate());

    context.addTraceLog(
        "Check the partition exists. Table:%s, partition:%s.", dataSourceId, schedulerPartition);
    boolean isPartitionTable = dataSourceMeta.isPartitionTable(source, project, table);
    if (!isPartitionTable) {
      hasPartition = true;
    } else {
      boolean hasSubPartition =
          dataSourceMeta.hasPartition(source, dataSourceId, partition, bizDate);
      if (hasSubPartition) {
        context.addTraceLog("Table partition(%s:%s) exists", dataSourceId, schedulerPartition);
        hasPartition = hasSubPartition;
      } else {
        context.addTraceLog(
            "Table partition(%s:%s) does not exist", dataSourceId, schedulerPartition);
      }
    }
    if (!hasPartition) {
      context.addTraceLog(
          "Table partitions(%s:%s) not generated", dataSourceId, schedulerPartition);
      return SchedulerEnum.TaskStatus.RUNNING;
    }
    long recordCount = dataSourceMeta.getRecordCount(source, dataSourceId, partition, bizDate);
    context.addTraceLog(
        "Table partitions(%s:%s) have been generated, with a total of %s records",
        dataSourceId, schedulerPartition, recordCount);

    if (recordCount > SINGLE_INSTANCE_IMPORT_LIMIT) {
      context.addTraceLog(
          "The current partition data volume %s is greater than the maximum limit of single instance import data volume %s",
          recordCount, SINGLE_INSTANCE_IMPORT_LIMIT);
      return SchedulerEnum.TaskStatus.ERROR;
    }
    if (0 == recordCount) {
      context.addTraceLog(
          "The partition data has been generated, but the data volume is 0. The instance status is set to FINISH");
      setInstanceFinished(
          context, SchedulerEnum.TaskStatus.FINISH, SchedulerEnum.InstanceStatus.FINISH);
      return SchedulerEnum.TaskStatus.FINISH;
    }
    return SchedulerEnum.TaskStatus.FINISH;
  }
}
