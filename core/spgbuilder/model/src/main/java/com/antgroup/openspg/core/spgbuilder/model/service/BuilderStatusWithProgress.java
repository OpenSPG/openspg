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

package com.antgroup.openspg.core.spgbuilder.model.service;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;

public class BuilderStatusWithProgress extends BaseValObj {

  private final JobInstStatusEnum status;

  private final BaseBuilderResult result;

  private final BuilderProgress progress;

  public BuilderStatusWithProgress(JobInstStatusEnum status) {
    this(status, null, null);
  }

  public BuilderStatusWithProgress(
      JobInstStatusEnum status, BaseBuilderResult result, BuilderProgress progress) {
    this.status = status;
    this.result = result;
    this.progress = progress;
  }

  public JobInstStatusEnum getStatus() {
    return status;
  }

  public BaseBuilderResult getResult() {
    return result;
  }

  public BuilderProgress getProgress() {
    return progress;
  }
}
