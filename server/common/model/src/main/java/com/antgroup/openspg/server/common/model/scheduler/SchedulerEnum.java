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
package com.antgroup.openspg.server.common.model.scheduler;

/** all scheduler dependent enum */
public interface SchedulerEnum {

  /** Instance Status enum */
  enum InstanceStatus {
    WAITING,
    RUNNING,
    SKIP,
    FINISH,
    TERMINATE,
    SET_FINISH;

    /** status is Finished */
    public static boolean isFinished(InstanceStatus status) {
      return InstanceStatus.FINISH.equals(status)
          || InstanceStatus.TERMINATE.equals(status)
          || InstanceStatus.SET_FINISH.equals(status)
          || InstanceStatus.SKIP.equals(status);
    }
  }

  /** Life Cycle Enum */
  enum LifeCycle {
    PERIOD,
    ONCE,
    REAL_TIME
  }

  /** Dependence Enum */
  enum Dependence {
    DEPENDENT,
    INDEPENDENT
  }

  /** Status Enum */
  enum Status {
    ENABLE,
    DISABLE
  }

  /** Task Status Enum */
  enum TaskStatus {
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

  /** Translate Enum */
  enum TranslateType {
    LOCAL_EXAMPLE("localExampleTranslate");

    private String type;

    TranslateType(String type) {
      this.type = type;
    }

    public String getType() {
      return type;
    }
  }
}
