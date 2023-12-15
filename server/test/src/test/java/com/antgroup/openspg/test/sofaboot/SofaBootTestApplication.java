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

package com.antgroup.openspg.test.sofaboot;

import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

/*@Configuration
@SpringBootApplication
@ComponentScan(basePackages = "com.antgroup.openspg")
@ImportResource({"classpath*:spring/*.xml"})
@PropertySource(value = "classpath:config/application.properties")
@EnableScheduling*/
public class SofaBootTestApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(SofaBootTestApplication.class);

  public static void main(String[] args) {
    HttpClientBootstrap.init(new ConnectionInfo("http://127.0.0.1:8887"));

    SpringApplication.run(SofaBootTestApplication.class, args);
    LOGGER.info("SOFABoot Application Started!!!");
  }
}
