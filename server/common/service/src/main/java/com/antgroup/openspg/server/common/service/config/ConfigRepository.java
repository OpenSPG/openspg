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
