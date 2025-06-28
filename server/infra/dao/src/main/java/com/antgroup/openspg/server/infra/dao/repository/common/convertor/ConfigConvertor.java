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

package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.server.common.model.config.Config;
import com.antgroup.openspg.server.infra.dao.dataobject.ConfigDO;

public class ConfigConvertor {

  public static Config toModel(ConfigDO configDO) {
    if (null == configDO) {
      return null;
    }
    Object config = null;
    String json = configDO.getConfig().trim(); // 去除头尾的空格
    if (json.startsWith("{") && json.endsWith("}")) {
      config = JSON.parseObject(json);
    } else if (json.startsWith("[") && json.endsWith("]")) {
      config = JSON.parseArray(json);
    } else {
      config = json;
    }
    return new Config(
        configDO.getId(),
        configDO.getConfigName(),
        configDO.getConfigId(),
        config,
        configDO.getResourceType());
  }

  public static ConfigDO toDO(Config config) {
    ConfigDO configDO = new ConfigDO();
    configDO.setId(config.getId());
    configDO.setUserNo(config.getUserNo());
    configDO.setProjectId(config.getProjectId());
    configDO.setConfigName(config.getConfigName());
    configDO.setConfigId(config.getConfigId());
    configDO.setConfig(JSON.toJSONString(config.getConfig()));
    configDO.setVersion(config.getVersion());
    configDO.setDescription(config.getDescription());
    configDO.setResourceId(config.getResourceId());
    configDO.setResourceType(config.getResourceType());
    return configDO;
  }
}
