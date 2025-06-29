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
import com.antgroup.openspg.server.common.model.usermodel.UserModel;
import com.antgroup.openspg.server.common.model.usermodel.UserModelDTO;
import com.antgroup.openspg.server.common.model.usermodel.UserModelQuery;
import java.util.List;
import java.util.Map;

public interface UserModelManager {

  /** insert user model */
  Long insert(UserModelDTO record, Map<String, Object> modelTypeMap);

  /** delete By Id */
  int deleteById(Long id);

  /** delete By Ids */
  int deleteByIds(List<Long> ids);

  /** update user model */
  Long update(UserModel record);

  /** get By id */
  UserModel getById(Long id);

  /** query By Condition */
  List<UserModel> query(UserModelQuery record);

  /** get By Provider And Name */
  List<UserModel> getByProviderAndName(String provider, String name);

  /**
   * update api key
   *
   * @param request
   * @return
   */
  Long updateApiKey(UserModelDTO request);

  /**
   * list
   *
   * @param modelType
   * @param queryStr
   * @param modelId
   * @param userNo
   * @return
   */
  List<Map<String, Object>> list(String modelType, String queryStr, String modelId, String userNo);

  /**
   * get by model unique id
   *
   * @param modelId
   * @return
   */
  JSONObject getByModelId(String modelId);

  /**
   * delete model
   *
   * @param id
   * @param modelId
   * @return
   */
  Boolean deleteModel(Long id, String modelId);

  /**
   * update model visibility
   *
   * @param modelId
   * @param visibility
   * @param customize
   * @return
   */
  Boolean updateModelVisibility(String modelId, String visibility, JSONObject customize);

  /**
   * get by instance id
   *
   * @param instanceId
   * @return
   */
  UserModel getByInstanceId(String instanceId);

  /**
   * update base info by ids
   *
   * @param ids
   * @param name
   * @param visibility
   * @param userNo
   * @param config
   * @return
   */
  Long updateBaseInfoByIds(
      List<Long> ids, String name, String visibility, String userNo, String config);

  /**
   * select user private or public
   *
   * @param userNo
   * @return
   */
  List<UserModel> selectUserPrivateOrPublic(String userNo);

  /**
   * desensitized field sensitive
   *
   * @param config
   * @return
   */
  JSONObject desensitizedFieldSensitive(JSONObject config);

  /**
   * sensitive field recovery
   *
   * @param config
   * @return
   */
  JSONObject sensitiveFieldRecovery(JSONObject config, JSONObject oldConfig);

  /**
   * get model type map
   *
   * @return
   */
  Map<String, Object> getModelTypeMap();

  /**
   * get model by name and model
   *
   * @param provider
   * @param name
   * @param visibility
   * @param model
   * @return
   */
  JSONObject getModelByProviderAndModel(
      String provider, String name, String visibility, String model);
}
