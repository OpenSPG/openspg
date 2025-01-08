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

import com.alibaba.fastjson.annotation.JSONField;
import com.antgroup.openspg.common.util.NetworkAddressUtils;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerInfoLog extends BaseModel {

  private static final long serialVersionUID = -7380727904455645196L;

  public SchedulerInfoLog() {}

  public SchedulerInfoLog(String status, String elog, Date rt, Date ft) {
    this.status = status;
    this.elog = elog;
    this.rt = rt;
    this.ft = ft;
    this.ip = NetworkAddressUtils.LOCAL_IP;
  }

  private String status;

  /** Exception log */
  private String elog;

  /** trigger time */
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date rt;

  /** finish time */
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date ft;

  private String ip;
}
