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

package com.antgroup.openspg.server.biz.common;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.api.facade.dto.common.request.ConfigRequest;
import com.antgroup.openspg.server.common.model.config.Config;

/** kag global configuration */
public interface ConfigManager {

  /**
   * get a config
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
   * create global config
   *
   * @param request
   * @return
   */
  Integer create(ConfigRequest request);

  /**
   * update global config
   *
   * @param request
   * @return
   */
  Integer update(ConfigRequest request);

  /**
   * set api key desensitization
   *
   * @param configStr
   * @return
   */
  String setApiKeyDesensitization(String configStr);

  /**
   * handle api key desensitization
   *
   * @param config
   * @param oldConfig
   */
  void handleApiKey(JSONObject config, String oldConfig);

  /**
   * generate llm id
   *
   * @param config
   */
  void generateLLMIdCompletionLLM(JSONObject config);

  /**
   * backward compatible
   *
   * @param config
   */
  void backwardCompatible(JSONObject config);

  /**
   * get llm id by config
   *
   * @param config
   * @return
   */
  String getLLMIdByConfig(JSONObject config);
  /**
   * check llm change
   *
   * @param oldConfig
   * @param config
   * @return
   */
  boolean isLLMChange(JSONObject oldConfig, JSONObject config);
}
