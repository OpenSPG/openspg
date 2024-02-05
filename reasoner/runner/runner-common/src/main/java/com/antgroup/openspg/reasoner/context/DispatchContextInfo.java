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

package com.antgroup.openspg.reasoner.context;

import com.antgroup.openspg.reasoner.task.TaskRecord;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DispatchContextInfo implements Serializable {
  private final TaskRecord taskRecord;

  private final Map<Class<? extends BaseContextInitializer>, Object> contextObjectMap =
      new HashMap<>();

  public DispatchContextInfo(
      TaskRecord taskRecord,
      Map<Class<? extends BaseContextInitializer>, Object> contextObjectMap) {
    this.taskRecord = taskRecord;
    this.contextObjectMap.putAll(contextObjectMap);
  }

  public Map<Class<? extends BaseContextInitializer>, Object> getContextObjectMap() {
    return contextObjectMap;
  }

  public TaskRecord getTaskRecord() {
    return taskRecord;
  }

  public boolean isEmpty() {
    return contextObjectMap.isEmpty();
  }
}
