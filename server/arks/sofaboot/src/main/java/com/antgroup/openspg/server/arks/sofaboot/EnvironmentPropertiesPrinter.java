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

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvironmentPropertiesPrinter {

  @EventListener
  public void handleContextRefreshed(ContextRefreshedEvent event) {
    ConfigurableEnvironment env =
        (ConfigurableEnvironment) event.getApplicationContext().getEnvironment();

    env.getPropertySources().stream()
        .filter(ps -> ps instanceof MapPropertySource)
        .map(ps -> ((MapPropertySource) ps).getSource().keySet())
        .flatMap(Collection::stream)
        .distinct()
        .sorted()
        .forEach(key -> log.info("{}={}", key, env.getProperty(key)));
  }
}
