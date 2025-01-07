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
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerTaskDO;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SchedulerTaskConvertor {

  public static SchedulerTaskDO toDO(SchedulerTask task) {
    if (null == task) {
      return null;
    }
    SchedulerTaskDO taskDO = DozerBeanMapperUtil.map(task, SchedulerTaskDO.class);
    if (task.getExtension() != null) {
      taskDO.setExtension(JSONObject.toJSONString(task.getExtension()));
    }
    return taskDO;
  }

  public static SchedulerTask toModel(SchedulerTaskDO schedulerTaskDO) {
    if (null == schedulerTaskDO) {
      return null;
    }

    SchedulerTask task = DozerBeanMapperUtil.map(schedulerTaskDO, SchedulerTask.class);
    if (StringUtils.isNotBlank(schedulerTaskDO.getExtension())) {
      task.setExtension(JSONObject.parseObject(schedulerTaskDO.getExtension()));
    }
    return task;
  }

  public static List<SchedulerTaskDO> toDoList(List<SchedulerTask> tasks) {
    if (tasks == null) {
      return null;
    }
    List<SchedulerTaskDO> dos = Lists.newArrayList();
    for (SchedulerTask task : tasks) {
      dos.add(toDO(task));
    }
    return dos;
  }

  public static List<SchedulerTask> toModelList(List<SchedulerTaskDO> schedulerTaskDOs) {
    if (schedulerTaskDOs == null) {
      return null;
    }
    List<SchedulerTask> tasks = Lists.newArrayList();
    for (SchedulerTaskDO schedulerTaskDO : schedulerTaskDOs) {
      tasks.add(toModel(schedulerTaskDO));
    }
    return tasks;
  }
}
