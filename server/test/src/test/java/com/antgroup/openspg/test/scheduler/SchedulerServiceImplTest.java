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

package com.antgroup.openspg.test.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.common.util.thread.ThreadUtils;
import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.MergeMode;
import com.antgroup.openspg.server.common.model.scheduler.Status;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.translate.TranslateEnum;
import com.antgroup.openspg.test.sofaboot.SofaBootTestApplication;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Scheduler Service Test */
@SpringBootTest(
    classes = SofaBootTestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SchedulerServiceImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImplTest.class);

  @Autowired SchedulerService schedulerService;
  @Autowired SchedulerInstanceService schedulerInstanceService;
  @Autowired SchedulerExecuteService schedulerExecuteService;

  @Before
  public void setUp() {
    HttpClientBootstrap.init(
        new ConnectionInfo("http://127.0.0.1:8887").setConnectTimeout(60000).setReadTimeout(60000));
  }

  /**
   * step 1: create Once Job to submit step 2: query all Jobs step 3: offline Job step 4: online Job
   * step 5: update Job step 6: execute Job step 7: get Instance to set Finish step 8: reRun
   * Instance and to stop step 9: reRun Instance and to trigger step 10: trigger Instance until it
   * ends step 11: get tasks step 12: delete Job;
   */
  @Test
  void submitOnceJob() {
    // step 1: create Job to submit
    SchedulerJob job = new SchedulerJob();
    job.setProjectId(0L);
    job.setName("Test Once Job");
    job.setCreateUser("test");
    job.setLifeCycle(LifeCycle.ONCE.name());
    job.setTranslate(TranslateEnum.LOCAL_DRY_RUN.name());
    job.setMergeMode(MergeMode.MERGE.name());
    job = schedulerService.submitJob(job);
    Long jobId = job.getId();
    assertTrue(jobId > 0);

    // step 2: query Jobs
    SchedulerJobQuery jobQuery = new SchedulerJobQuery();
    jobQuery.setId(jobId);
    List<SchedulerJob> jobs = schedulerService.searchJobs(jobQuery).getData();
    assertEquals(1, jobs.size());
    // step 3: offline job
    assertTrue(schedulerService.disableJob(jobId));
    job = schedulerService.getJobById(jobId);
    assertEquals(Status.OFFLINE.name(), job.getStatus());
    SchedulerInstanceQuery instanceQuery = new SchedulerInstanceQuery();
    instanceQuery.setJobId(jobId);
    List<SchedulerInstance> notFinishInstances =
        schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertTrue(CollectionUtils.isEmpty(notFinishInstances));
    // step 4: online Job
    assertTrue(schedulerService.enableJob(jobId));
    job = schedulerService.getJobById(jobId);
    assertEquals(Status.ONLINE.name(), job.getStatus());
    // step 5: update Job
    String updateName = "Update Test Once Job";
    job.setName(updateName);
    assertTrue(schedulerService.updateJob(job));
    job = schedulerService.getJobById(jobId);
    assertEquals(updateName, job.getName());

    // step 6: execute Job
    assertTrue(schedulerService.executeJob(jobId));
    notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertEquals(1, notFinishInstances.size());

    // step 7: get Instance to set Finish
    List<SchedulerInstance> instances = schedulerService.searchInstances(instanceQuery).getData();
    assertTrue(instances.size() > 0);
    SchedulerInstance instance = notFinishInstances.get(0);
    SchedulerInstance ins = schedulerService.getInstanceById(instance.getId());
    assertEquals(ins.getId(), instance.getId());
    assertTrue(schedulerService.setFinishInstance(instance.getId()));
    notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertTrue(CollectionUtils.isEmpty(notFinishInstances));

    // step 8: reRun Instance and to stop
    assertTrue(schedulerService.restartInstance(instance.getId()));
    notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertEquals(1, notFinishInstances.size());
    instance = notFinishInstances.get(0);
    assertTrue(schedulerService.stopInstance(instance.getId()));
    notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertTrue(CollectionUtils.isEmpty(notFinishInstances));

    // step 9: reRun Instance
    assertTrue(schedulerService.restartInstance(instance.getId()));
    notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertEquals(1, notFinishInstances.size());
    instance = notFinishInstances.get(0);

    ThreadUtils.sleep(5000);
    // step 10: trigger Instance until it ends
    while (!InstanceStatus.isFinished(getInstance(instance.getId()))) {
      assertTrue(schedulerService.triggerInstance(instance.getId()));
      ThreadUtils.sleep(5000);
    }
    instance = schedulerService.getInstanceById(instance.getId());
    assertEquals(InstanceStatus.FINISH.name(), instance.getStatus());

    // step 11: get tasks
    SchedulerTaskQuery taskQuery = new SchedulerTaskQuery();
    taskQuery.setInstanceId(instance.getId());
    List<SchedulerTask> tasks = schedulerService.searchTasks(taskQuery).getData();
    assertTrue(tasks.size() > 0);
    for (SchedulerTask task : tasks) {
      LOGGER.info(
          String.format(
              "|task|%s|%s|%s|%s|%s|%s|%s|%s|%s",
              task.getTitle(),
              task.getType(),
              task.getStatus(),
              task.getJobId(),
              task.getInstanceId(),
              task.getExecuteNum(),
              DateTimeUtils.getDate2LongStr(task.getBeginTime()),
              DateTimeUtils.getDate2LongStr(task.getFinishTime()),
              task.getRemark()));
    }

    // step 12: delete Job
    assertTrue(schedulerService.deleteJob(jobId));
    jobs = schedulerService.searchJobs(jobQuery).getData();
    assertEquals(0, jobs.size());
    instances = schedulerService.searchInstances(instanceQuery).getData();
    assertEquals(0, instances.size());
    tasks = schedulerService.searchTasks(taskQuery).getData();
    assertEquals(0, tasks.size());
  }

  /**
   * step 1: create Period Job to submit step 2: query Jobs and Instances step 3: offline Job step
   * 4: online Job step 5: execute Job step 6: trigger first Instance until it ends step 7: trigger
   * second Instance until it ends step 8: get tasks step 9: delete Job
   */
  @Test
  void submitPeriodJob() {
    // step 1: create Period Job to submit
    SchedulerJob job = new SchedulerJob();
    job.setProjectId(0L);
    job.setName("Test Period Job");
    job.setCreateUser("test");
    job.setLifeCycle(LifeCycle.PERIOD.name());
    job.setSchedulerCron("0 0 * * * ?");
    job.setTranslate(TranslateEnum.LOCAL_DRY_RUN.name());
    job.setMergeMode(MergeMode.MERGE.name());
    job = schedulerService.submitJob(job);
    Long jobId = job.getId();
    assertTrue(jobId > 0);

    // step 2: query Jobs and Instances
    SchedulerJobQuery jobQuery = new SchedulerJobQuery();
    jobQuery.setId(jobId);
    List<SchedulerJob> jobs = schedulerService.searchJobs(jobQuery).getData();
    assertEquals(1, jobs.size());
    SchedulerInstanceQuery instanceQuery = new SchedulerInstanceQuery();
    instanceQuery.setJobId(jobId);
    List<SchedulerInstance> instances = schedulerService.searchInstances(instanceQuery).getData();
    assertEquals(24, instances.size());

    // step 3: offline Period job
    assertTrue(schedulerService.disableJob(jobId));
    job = schedulerService.getJobById(jobId);
    assertEquals(Status.OFFLINE.name(), job.getStatus());
    List<SchedulerInstance> notFinishInstances =
        schedulerInstanceService.getNotFinishInstance(instanceQuery);
    LOGGER.info(String.format("notFinishInstances size %s", notFinishInstances.size()));
    assertTrue(CollectionUtils.isEmpty(notFinishInstances));

    // step 4: online Period Job
    assertTrue(schedulerService.enableJob(jobId));
    job = schedulerService.getJobById(jobId);
    assertEquals(Status.ONLINE.name(), job.getStatus());

    // step 5: execute Job
    assertFalse(schedulerService.executeJob(jobId));
    notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertTrue(notFinishInstances.size() < 24);
    schedulerInstanceService.deleteByJobId(jobId);
    assertTrue(schedulerService.executeJob(jobId));
    notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertEquals(24, notFinishInstances.size());

    SchedulerInstance instance =
        notFinishInstances.stream()
            .min(Comparator.comparing(x -> x.getSchedulerDate()))
            .orElse(null);
    ThreadUtils.sleep(5000);

    // step 6: trigger first Instance until it ends
    while (schedulerInstanceService.getNotFinishInstance(instanceQuery).size() == 24) {
      schedulerExecuteService.executeInstances();
      ThreadUtils.sleep(5000);
    }
    instance = schedulerService.getInstanceById(instance.getId());
    assertEquals(InstanceStatus.FINISH.name(), instance.getStatus());

    // step 7: trigger second Instance until it ends
    notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertEquals(23, notFinishInstances.size());
    instance =
        notFinishInstances.stream()
            .min(Comparator.comparing(x -> x.getSchedulerDate()))
            .orElse(null);
    while (schedulerInstanceService.getNotFinishInstance(instanceQuery).size() == 23) {
      schedulerExecuteService.executeInstances();
      ThreadUtils.sleep(5000);
    }
    instance = schedulerService.getInstanceById(instance.getId());
    assertEquals(InstanceStatus.FINISH.name(), instance.getStatus());

    // step 8: get tasks
    SchedulerTaskQuery taskQuery = new SchedulerTaskQuery();
    taskQuery.setInstanceId(instance.getId());
    List<SchedulerTask> tasks = schedulerService.searchTasks(taskQuery).getData();
    assertTrue(tasks.size() > 0);
    for (SchedulerTask task : tasks) {
      LOGGER.info(
          String.format(
              "|task|%s|%s|%s|%s|%s|%s|%s|%s|%s",
              task.getTitle(),
              task.getType(),
              task.getStatus(),
              task.getJobId(),
              task.getInstanceId(),
              task.getExecuteNum(),
              DateTimeUtils.getDate2LongStr(task.getBeginTime()),
              DateTimeUtils.getDate2LongStr(task.getFinishTime()),
              task.getRemark()));
    }

    // step 9: delete Job
    assertTrue(schedulerService.deleteJob(jobId));
    jobs = schedulerService.searchJobs(jobQuery).getData();
    assertEquals(0, jobs.size());
    instances = schedulerService.searchInstances(instanceQuery).getData();
    assertEquals(0, instances.size());
    tasks = schedulerService.searchTasks(taskQuery).getData();
    assertEquals(0, tasks.size());
  }

  /**
   * step 1: create RealTime Job to submit step 2: query Jobs and Instances step 3: offline Job step
   * 4: online Job step 5: trigger Instance step 6: get tasks step 7: delete Job;
   */
  @Test
  void submitRealTimeJob() {
    // step 1: create RealTime Job to submit
    SchedulerJob job = new SchedulerJob();
    job.setProjectId(0L);
    job.setName("Test RealTime Job");
    job.setCreateUser("test");
    job.setLifeCycle(LifeCycle.REAL_TIME.name());
    job.setTranslate(TranslateEnum.LOCAL_DRY_RUN.name());
    job.setMergeMode(MergeMode.MERGE.name());
    job = schedulerService.submitJob(job);
    Long jobId = job.getId();
    assertTrue(jobId > 0);

    // step 2: query Jobs and Instances
    SchedulerJobQuery jobQuery = new SchedulerJobQuery();
    jobQuery.setId(jobId);
    List<SchedulerJob> jobs = schedulerService.searchJobs(jobQuery).getData();
    assertEquals(1, jobs.size());
    SchedulerInstanceQuery instanceQuery = new SchedulerInstanceQuery();
    instanceQuery.setJobId(jobId);
    List<SchedulerInstance> instances = schedulerService.searchInstances(instanceQuery).getData();
    assertEquals(1, instances.size());

    // step 3: offline RealTime job
    assertTrue(schedulerService.disableJob(jobId));
    job = schedulerService.getJobById(jobId);
    assertEquals(Status.OFFLINE.name(), job.getStatus());
    List<SchedulerInstance> notFinishInstances =
        schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertTrue(CollectionUtils.isEmpty(notFinishInstances));

    // step 4: online RealTime Job
    assertTrue(schedulerService.enableJob(jobId));
    job = schedulerService.getJobById(jobId);
    assertEquals(Status.ONLINE.name(), job.getStatus());
    notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
    assertEquals(1, notFinishInstances.size());

    SchedulerInstance instance =
        notFinishInstances.stream()
            .min(Comparator.comparing(x -> x.getSchedulerDate()))
            .orElse(null);
    ThreadUtils.sleep(5000);

    // step 5: trigger Instance
    for (int i = 0; i < 10; i++) {
      assertTrue(schedulerService.triggerInstance(instance.getId()));
      ThreadUtils.sleep(5000);
    }
    instance = schedulerService.getInstanceById(instance.getId());
    assertEquals(InstanceStatus.RUNNING.name(), instance.getStatus());

    // step 6: get tasks
    SchedulerTaskQuery taskQuery = new SchedulerTaskQuery();
    taskQuery.setInstanceId(instance.getId());
    List<SchedulerTask> tasks = schedulerService.searchTasks(taskQuery).getData();
    assertTrue(tasks.size() > 0);
    for (SchedulerTask task : tasks) {
      LOGGER.info(
          String.format(
              "|task|%s|%s|%s|%s|%s|%s|%s|%s|%s",
              task.getTitle(),
              task.getType(),
              task.getStatus(),
              task.getJobId(),
              task.getInstanceId(),
              task.getExecuteNum(),
              DateTimeUtils.getDate2LongStr(task.getBeginTime()),
              DateTimeUtils.getDate2LongStr(task.getFinishTime()),
              task.getRemark()));
    }

    // step 7: delete Job
    assertTrue(schedulerService.deleteJob(jobId));
  }

  private String getInstance(Long id) {
    SchedulerInstance ins = schedulerService.getInstanceById(id);
    return ins.getStatus();
  }
}
