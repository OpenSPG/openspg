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

package com.antgroup.openspg.common.model.job;

import com.google.common.collect.Sets;
import java.util.Set;

public enum JobInstStatusEnum {
  INIT,
  QUEUE,
  RUNNING,
  CANCEL,
  FAILURE,
  SUCCESS,
  ;

  public static final Set<JobInstStatusEnum> FINISHED_STATUS =
      Sets.newHashSet(CANCEL, FAILURE, SUCCESS);

  public static final Set<JobInstStatusEnum> RUNNING_STATUS = Sets.newHashSet(RUNNING);

  public boolean isFinished() {
    return FINISHED_STATUS.contains(this);
  }

  public boolean isRunning() {
    return RUNNING_STATUS.contains(this);
  }
}
