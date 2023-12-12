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
  /** wait */
  WAIT,
  /** running */
  RUNNING,
  /** finish */
  FINISH,
  /** error */
  ERROR,
  /** skip */
  SKIP,
  /** terminate */
  TERMINATE,
  /** set finish */
  SET_FINISH;

  /** get TaskStatus by name */
  public static TaskStatus getByName(String name, TaskStatus defaultValue) {
    for (TaskStatus workflowStatus : TaskStatus.values()) {
      if (workflowStatus.name().equalsIgnoreCase(name)) {
        return workflowStatus;
      }
    }
    return defaultValue;
  }

  /** get by name, return null if the enum does not exist */
  public static TaskStatus getByName(String name) {
    return getByName(name, null);
  }

  /** status is Finished by TaskStatus */
  public static boolean isFinished(TaskStatus status) {
    return TaskStatus.FINISH.equals(status)
        || TaskStatus.SKIP.equals(status)
        || TaskStatus.TERMINATE.equals(status)
        || TaskStatus.SET_FINISH.equals(status);
  }

  /** status is Finished by String */
  public static boolean isFinished(String status) {
    return isFinished(getByName(status));
  }

  /** status is Running by TaskStatus */
  public static boolean isRunning(TaskStatus status) {
    return TaskStatus.RUNNING.equals(status) || TaskStatus.ERROR.equals(status);
  }

  /** status is Running by String */
  public static boolean isRunning(String status) {
    return isRunning(getByName(status));
  }
}
