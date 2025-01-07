package com.antgroup.openspg.server.infra.dao.repository.common;

import com.antgroup.openspg.server.common.model.config.Config;
import com.antgroup.openspg.server.common.service.config.ConfigRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.ConfigDO;
import com.antgroup.openspg.server.infra.dao.mapper.ConfigMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.ConfigConvertor;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigRepositoryImpl implements ConfigRepository {

  @Autowired private ConfigMapper configMapper;

  @Override
  public Config query(String configId, String version) {
    ConfigDO configDO = configMapper.selectByConfigIdAndVersion(configId, version);
    return ConfigConvertor.toModel(configDO);
  }

  @Override
  public Config getById(Long id) {
    ConfigDO configDO = configMapper.selectByPrimaryKey(id);
    return ConfigConvertor.toModel(configDO);
  }

  @Override
  public Integer save(Config config) {
    if (null == config) {
      return 0;
    }
    ConfigDO configDO = ConfigConvertor.toDO(config);
    configDO.setGmtCreate(new Date());
    configDO.setGmtModified(new Date());
    configDO.setStatus(1);
    return configMapper.insert(configDO);
  }

  @Override
  public Integer update(Config config) {
    if (null == config || null == config.getId()) {
      return 0;
    }
    ConfigDO configDO = ConfigConvertor.toDO(config);
    configDO.setGmtModified(new Date());
    configDO.setStatus(1);
    return configMapper.updateByPrimaryKeySelective(configDO);
  }
}
