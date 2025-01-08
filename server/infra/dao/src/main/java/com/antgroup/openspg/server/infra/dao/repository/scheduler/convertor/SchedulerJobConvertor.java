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

package com.antgroup.openspg.server.infra.dao.repository.scheduler.convertor;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.DozerBeanMapperUtil;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerJobDO;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SchedulerJobConvertor {

  public static SchedulerJobDO toDO(SchedulerJob job) {
    if (null == job) {
      return null;
    }
    SchedulerJobDO jobDO = DozerBeanMapperUtil.map(job, SchedulerJobDO.class);
    if (job.getExtension() != null) {
      jobDO.setExtension(JSONObject.toJSONString(job.getExtension()));
    }
    return jobDO;
  }

  public static SchedulerJob toModel(SchedulerJobDO schedulerJobDO) {
    if (null == schedulerJobDO) {
      return null;
    }

    SchedulerJob job = DozerBeanMapperUtil.map(schedulerJobDO, SchedulerJob.class);
    if (StringUtils.isNotBlank(schedulerJobDO.getExtension())) {
      job.setExtension(JSONObject.parseObject(schedulerJobDO.getExtension()));
    }
    return job;
  }

  public static List<SchedulerJobDO> toDoList(List<SchedulerJob> jobs) {
    if (jobs == null) {
      return null;
    }
    List<SchedulerJobDO> dos = Lists.newArrayList();
    for (SchedulerJob job : jobs) {
      dos.add(toDO(job));
    }
    return dos;
  }

  public static List<SchedulerJob> toModelList(List<SchedulerJobDO> schedulerJobDOs) {
    if (schedulerJobDOs == null) {
      return null;
    }
    List<SchedulerJob> jobs = Lists.newArrayList();
    for (SchedulerJobDO schedulerJobDO : schedulerJobDOs) {
      jobs.add(toModel(schedulerJobDO));
    }
    return jobs;
  }
}
