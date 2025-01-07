package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.antgroup.openspg.server.common.model.config.Config;
import com.antgroup.openspg.server.infra.dao.dataobject.ConfigDO;

public class ConfigConvertor {

  public static Config toModel(ConfigDO configDO) {
    if (null == configDO) {
      return null;
    }
    return new Config(
        configDO.getId(),
        configDO.getConfigName(),
        configDO.getConfigId(),
        configDO.getConfig(),
        configDO.getResourceType());
  }

  public static ConfigDO toDO(Config config) {
    ConfigDO configDO = new ConfigDO();
    configDO.setId(config.getId());
    configDO.setUserNo(config.getUserNo());
    configDO.setProjectId(config.getProjectId());
    configDO.setConfigName(config.getConfigName());
    configDO.setConfigId(config.getConfigId());
    configDO.setConfig(config.getConfig());
    configDO.setVersion(config.getVersion());
    configDO.setDescription(config.getDescription());
    configDO.setResourceId(config.getResourceId());
    configDO.setResourceType(config.getResourceType());
    return configDO;
  }
}
