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

package com.antgroup.openspg.server.common.service.config;

import com.antgroup.openspg.server.common.model.config.Config;

/** global config repository */
public interface ConfigRepository {

  /**
   * query a global config by configId and version
   *
   * @param configId
   * @param version
   * @return
   */
  Config query(String configId, String version);

  /**
   * get a config by id
   *
   * @param id
   * @return
   */
  Config getById(Long id);

  /**
   * save a global config
   *
   * @param config
   * @return
   */
  Integer save(Config config);

  /**
   * update a global config
   *
   * @param config
   * @return
   */
  Integer update(Config config);
}
