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
  /** submit job */
  SchedulerJob submitJob(SchedulerJob job);

  /** execute Job */
  Boolean executeJob(Long id);

  /** enable Job */
  Boolean enableJob(Long id);

  /** disable Job */
  Boolean disableJob(Long id);

  /** delete Job */
  Boolean deleteJob(Long id);

  /** update Job */
  boolean updateJob(SchedulerJob job);

  /** get Job By id */
  SchedulerJob getJobById(Long id);

  /** search Jobs */
  List<SchedulerJob> searchJobs(SchedulerJob query);

  /** get Instance By id */
  SchedulerInstance getInstanceById(Long id);

  /** stop Instance */
  Boolean stopInstance(Long id);

  /** set Instance To Finish */
  Boolean setFinishInstance(Long id);

  /** restart Instance */
  Boolean restartInstance(Long id);

  /** trigger Instance */
  Boolean triggerInstance(Long id);

  /** search Instances */
  List<SchedulerInstance> searchInstances(SchedulerInstance query);

  /** search Tasks */
  List<SchedulerTask> searchTasks(SchedulerTask query);
}
