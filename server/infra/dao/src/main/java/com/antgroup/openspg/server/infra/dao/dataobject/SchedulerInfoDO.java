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
package com.antgroup.openspg.server.infra.dao.dataobject;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerInfoDO {

  /** primary key */
  private Long id;

  /** Create time */
  private Date gmtCreate;

  /** Modified time */
  private Date gmtModified;

  /** name */
  private String name;

  /** status */
  private String status;

  /** Scheduler period Unit: second */
  private Long period;

  /** execute count */
  private Integer count;

  /** log */
  private String log;

  /** config */
  private String config;

  /** lock Time */
  private Date lockTime;
}
