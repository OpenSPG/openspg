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
package com.antgroup.openspg.server.core.scheduler.service.engine;

/** Scheduler Execute Service. execute all instances */
public interface SchedulerExecuteService {
  /** generate instances by period job */
  void generateInstances();

  /** execute all not finish instances */
  void executeInstances();

  /** execute instance by id */
  void executeInstance(Long id);
}
