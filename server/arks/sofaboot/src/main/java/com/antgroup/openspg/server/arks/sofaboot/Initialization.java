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

package com.antgroup.openspg.server.arks.sofaboot;

import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import com.antgroup.openspg.server.common.service.spring.SpringContextAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Initialization extends SpringContextAware {

  @Autowired private AppEnvConfig appEnvConfig;

  @Override
  public void init() {
    initHttpClientBootstrap();
    initScheduler();
  }

  private void initHttpClientBootstrap() {
    HttpClientBootstrap.init(
        new ConnectionInfo(appEnvConfig.getSchemaUri())
            .setConnectTimeout(60000)
            .setReadTimeout(60000));
  }

  private void initScheduler() {
    try {
      Class.forName(
          "com.antgroup.openspg.cloudext.interfaces."
              + "jobscheduler.JobSchedulerClientDriverManager");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
