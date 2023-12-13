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
package com.antgroup.openspg.server.core.scheduler.model.query;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import lombok.Getter;
import lombok.Setter;

/** Scheduler Job Query Model */
@Getter
@Setter
public class SchedulerJobQuery extends SchedulerJob {

  private static final long serialVersionUID = -857200975331899039L;

  /** page No */
  private Integer pageNo;
  /** page Size */
  private Integer pageSize;
}
