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
   * clear redundant field
   *
   * @param jsonObject model json config
   */
  JSONObject clearRedundantField(JSONObject jsonObject, String configType);
}
