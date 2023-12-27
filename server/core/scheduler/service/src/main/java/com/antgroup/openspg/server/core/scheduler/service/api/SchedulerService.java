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
package com.antgroup.openspg.server.core.scheduler.service.api;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import java.util.List;

/** Scheduler Service:submit,execute,delete and other scheduler interfaces */
public interface SchedulerService {
  /** to save job. Execute once after saving successfully*/
  SchedulerJob submitJob(SchedulerJob job);

  /** execute once Job, generate instance */
  Boolean executeJob(Long jobId);

  /** enable Job, set job status to enable */
  Boolean enableJob(Long jobId);

  /** disable Job, set job status to disable */
  Boolean disableJob(Long jobId);

  /** delete Job, delete associated instances and tasks together */
  Boolean deleteJob(Long jobId);

  /** update Job fields */
  boolean updateJob(SchedulerJob job);

  /** get Job details By id */
  SchedulerJob getJobById(Long jobId);

  /** search Jobs by fields */
  List<SchedulerJob> searchJobs(SchedulerJob query);

  /** get Instance details By id */
  SchedulerInstance getInstanceById(Long instanceId);

  /** stop Instance, set instance status to terminate */
  Boolean stopInstance(Long instanceId);

  /** set Instance To Finish, set instance status to finish */
  Boolean setFinishInstance(Long instanceId);

  /** restart Instance, generate a new instance and execute */
  Boolean restartInstance(Long instanceId);

  /** trigger instance to execute once */
  Boolean triggerInstance(Long instanceId);

  /** search Instances by fields */
  List<SchedulerInstance> searchInstances(SchedulerInstance query);

  /** search Tasks by fields */
  List<SchedulerTask> searchTasks(SchedulerTask query);
}
