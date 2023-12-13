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
package com.antgroup.openspg.server.common.model.scheduler;

/** Task Status Enum */
public enum TaskStatus {
  WAIT,
  RUNNING,
  FINISH,
  ERROR,
  SKIP,
  TERMINATE,
  SET_FINISH;

  /** status is Finished by TaskStatus */
  public static boolean isFinished(TaskStatus status) {
    return TaskStatus.FINISH.equals(status)
        || TaskStatus.SKIP.equals(status)
        || TaskStatus.TERMINATE.equals(status)
        || TaskStatus.SET_FINISH.equals(status);
  }

  /** status is Running by TaskStatus */
  public static boolean isRunning(TaskStatus status) {
    return TaskStatus.RUNNING.equals(status) || TaskStatus.ERROR.equals(status);
  }

}
