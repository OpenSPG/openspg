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
package com.antgroup.openspg.server.core.scheduler.model.service;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.SchedulerInfoStatus;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Scheduler Info Model */
@Getter
@Setter
public class SchedulerInfo extends BaseModel {

  private static final long serialVersionUID = 8374591289230111738L;

  public static final String WHITE_IP_KEY = "whiteIps";

  public static final String HOST_EXCEPTION_TIMEOUT_KEY = "hostExceptionTimeout";

  /** primary key */
  private Long id;

  /** Create time */
  private Date gmtCreate;

  /** Modified time */
  private Date gmtModified;

  /** name */
  private String name;

  /** status */
  private SchedulerInfoStatus status;

  /** Scheduler period Unit: second */
  private Long period;

  /** execute count */
  private Integer count;

  /** log */
  private List<SchedulerInfoLog> log;

  /** config */
  private JSONObject config;

  /** lock Time */
  private Date lockTime;

  public List<String> getWhiteIps() {
    if (config == null || !config.containsKey(WHITE_IP_KEY)) {
      return Lists.newArrayList();
    }
    return JSONObject.parseArray(config.getString(WHITE_IP_KEY), String.class);
  }

  public Long getHostExceptionTimeout() {
    if (config == null || !config.containsKey(HOST_EXCEPTION_TIMEOUT_KEY)) {
      return 0L;
    }
    return config.getLong(HOST_EXCEPTION_TIMEOUT_KEY);
  }
}
