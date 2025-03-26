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
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.common.util.DozerBeanMapperUtil;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInfo;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInfoLog;
import com.antgroup.openspg.server.infra.dao.dataobject.SchedulerInfoDO;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SchedulerInfoConvertor {

  public static SchedulerInfoDO toDO(SchedulerInfo info) {
    if (null == info) {
      return null;
    }
    SchedulerInfoDO infoDO = DozerBeanMapperUtil.map(info, SchedulerInfoDO.class);
    if (info.getConfig() != null) {
      infoDO.setConfig(JSONObject.toJSONString(info.getConfig()));
    }
    if (info.getLog() != null) {
      infoDO.setLog(JSONObject.toJSONString(info.getLog()));
    }
    return infoDO;
  }

  public static SchedulerInfo toModel(SchedulerInfoDO schedulerInfoDO) {
    if (null == schedulerInfoDO) {
      return null;
    }
    SchedulerInfo info = DozerBeanMapperUtil.map(schedulerInfoDO, SchedulerInfo.class);
    if (StringUtils.isNotBlank(schedulerInfoDO.getConfig())) {
      info.setConfig(JSONObject.parseObject(schedulerInfoDO.getConfig()));
    }
    if (StringUtils.isNotBlank(schedulerInfoDO.getLog())) {
      info.setLog(
          JSONObject.parseObject(
              schedulerInfoDO.getLog(), new TypeReference<List<SchedulerInfoLog>>() {}));
    }
    return info;
  }

  public static List<SchedulerInfoDO> toDoList(List<SchedulerInfo> infos) {
    if (infos == null) {
      return null;
    }
    List<SchedulerInfoDO> dos = Lists.newArrayList();
    for (SchedulerInfo info : infos) {
      dos.add(toDO(info));
    }
    return dos;
  }

  public static List<SchedulerInfo> toModelList(List<SchedulerInfoDO> schedulerInfoDOs) {
    if (schedulerInfoDOs == null) {
      return null;
    }
    List<SchedulerInfo> infos = Lists.newArrayList();
    for (SchedulerInfoDO schedulerInfoDO : schedulerInfoDOs) {
      infos.add(toModel(schedulerInfoDO));
    }
    return infos;
  }
}
