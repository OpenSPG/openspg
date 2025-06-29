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
package com.antgroup.openspg.server.common.service.usermodel;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.common.model.usermodel.UserModel;
import com.antgroup.openspg.server.common.model.usermodel.UserModelQuery;
import java.util.List;

public interface UserModelRepository {

  /** insert user model */
  Long insert(UserModel record);

  /** delete By Id */
  int deleteById(Long id);

  /** delete By Ids */
  int deleteByIds(List<Long> ids);

  /** update user model */
  Long update(UserModel record);

  /** get By id */
  UserModel getById(Long id);

  /**
   * get by instance id
   *
   * @param instanceId
   * @return
   */
  UserModel getByInstanceId(String instanceId);

  /** query By Condition */
  List<UserModel> query(UserModelQuery record);

  /** get By Provider And Name */
  List<UserModel> getByProviderAndName(String provider, String name);

  /**
   * select user private or public
   *
   * @param userNo
   * @return
   */
  List<UserModel> selectUserPrivateOrPublic(String userNo);

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
   * get by model unique id
   *
   * @param modelId
   * @return
   */
  JSONObject getByModelId(String modelId);
}
