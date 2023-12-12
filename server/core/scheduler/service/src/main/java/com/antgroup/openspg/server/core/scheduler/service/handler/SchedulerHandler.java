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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.handler;

/**
 * @author yangjin
 * @version : SchedulerHandler.java, v 0.1 2023年11月30日 18:33 yangjin Exp $
 */
public interface SchedulerHandler {

  /**
   * scheduler timer entrance. execute Instances
   *
   * @return
   */
  void executeInstances();

  /**
   * scheduler generate Instances timer
   *
   * @return
   */
  void generateInstances();
}
