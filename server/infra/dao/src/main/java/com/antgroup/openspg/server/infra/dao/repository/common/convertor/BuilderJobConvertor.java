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

package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.antgroup.openspg.common.util.DozerBeanMapperUtil;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.infra.dao.dataobject.BuilderJobDO;
import com.google.common.collect.Lists;
import java.util.List;

public class BuilderJobConvertor {

  public static BuilderJobDO toDO(BuilderJob job) {
    if (null == job) {
      return null;
    }
    BuilderJobDO jobDO = DozerBeanMapperUtil.map(job, BuilderJobDO.class);
    return jobDO;
  }

  public static BuilderJob toModel(BuilderJobDO schedulerJobDO) {
    if (null == schedulerJobDO) {
      return null;
    }

    BuilderJob job = DozerBeanMapperUtil.map(schedulerJobDO, BuilderJob.class);
    return job;
  }

  public static List<BuilderJobDO> toDoList(List<BuilderJob> jobs) {
    if (jobs == null) {
      return null;
    }
    List<BuilderJobDO> dos = Lists.newArrayList();
    for (BuilderJob job : jobs) {
      dos.add(toDO(job));
    }
    return dos;
  }

  public static List<BuilderJob> toModelList(List<BuilderJobDO> schedulerJobDOs) {
    if (schedulerJobDOs == null) {
      return null;
    }
    List<BuilderJob> jobs = Lists.newArrayList();
    for (BuilderJobDO schedulerJobDO : schedulerJobDOs) {
      jobs.add(toModel(schedulerJobDO));
    }
    return jobs;
  }
}
