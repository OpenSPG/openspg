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
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerInstanceDO;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SchedulerInstanceConvertor {

  public static SchedulerInstanceDO toDO(SchedulerInstance instance) {
    if (null == instance) {
      return null;
    }
    SchedulerInstanceDO instanceDO = DozerBeanMapperUtil.map(instance, SchedulerInstanceDO.class);
    if (instance.getExtension() != null) {
      instanceDO.setExtension(JSONObject.toJSONString(instance.getExtension()));
    }
    if (instance.getTaskDag() != null) {
      instanceDO.setTaskDag(JSONObject.toJSONString(instance.getTaskDag()));
    }
    return instanceDO;
  }

  public static SchedulerInstance toModel(SchedulerInstanceDO schedulerInstanceDO) {
    if (null == schedulerInstanceDO) {
      return null;
    }

    SchedulerInstance instance =
        DozerBeanMapperUtil.map(schedulerInstanceDO, SchedulerInstance.class);
    if (StringUtils.isNotBlank(schedulerInstanceDO.getExtension())) {
      instance.setExtension(JSONObject.parseObject(schedulerInstanceDO.getExtension()));
    }
    if (StringUtils.isNotBlank(schedulerInstanceDO.getTaskDag())) {
      instance.setTaskDag(
          JSONObject.parseObject(schedulerInstanceDO.getTaskDag(), TaskExecuteDag.class));
    }
    return instance;
  }

  public static List<SchedulerInstanceDO> toDoList(List<SchedulerInstance> instances) {
    if (instances == null) {
      return null;
    }
    List<SchedulerInstanceDO> dos = Lists.newArrayList();
    for (SchedulerInstance instance : instances) {
      dos.add(toDO(instance));
    }
    return dos;
  }

  public static List<SchedulerInstance> toModelList(
      List<SchedulerInstanceDO> schedulerInstanceDOs) {
    if (schedulerInstanceDOs == null) {
      return null;
    }
    List<SchedulerInstance> instances = Lists.newArrayList();
    for (SchedulerInstanceDO schedulerInstanceDO : schedulerInstanceDOs) {
      instances.add(toModel(schedulerInstanceDO));
    }
    return instances;
  }
}
