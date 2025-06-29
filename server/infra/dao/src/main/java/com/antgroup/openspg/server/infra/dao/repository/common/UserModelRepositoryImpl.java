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
package com.antgroup.openspg.server.infra.dao.repository.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspg.common.util.JsonUtils;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.provider.ModelProvider;
import com.antgroup.openspg.server.common.model.usermodel.UserModel;
import com.antgroup.openspg.server.common.model.usermodel.UserModelQuery;
import com.antgroup.openspg.server.common.service.provider.ModelProviderRepository;
import com.antgroup.openspg.server.common.service.usermodel.UserModelRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.UserModelDO;
import com.antgroup.openspg.server.infra.dao.mapper.UserModelDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.UserModelConvertor;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserModelRepositoryImpl implements UserModelRepository {

  @Autowired private UserModelDOMapper userModelDOMapper;

  @Autowired private ModelProviderRepository modelProviderRepository;

  @Override
  public Long insert(UserModel record) {
    UserModelDO userModelDO = UserModelConvertor.toDO(record);
    userModelDOMapper.insert(userModelDO);
    record.setId(userModelDO.getId());
    return record.getId();
  }

  @Override
  public int deleteById(Long id) {
    return userModelDOMapper.deleteById(id);
  }

  @Override
  public int deleteByIds(List<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0;
    }
    return userModelDOMapper.deleteByIds(ids);
  }

  @Override
  public Long update(UserModel record) {
    return userModelDOMapper.update(UserModelConvertor.toDO(record));
  }

  @Override
  public UserModel getById(Long id) {
    return UserModelConvertor.toModel(userModelDOMapper.getById(id));
  }

  @Override
  public UserModel getByInstanceId(String instanceId) {
    return UserModelConvertor.toModel(userModelDOMapper.getByInstanceId(instanceId));
  }

  @Override
  public List<UserModel> query(UserModelQuery record) {
    return UserModelConvertor.toModelList(userModelDOMapper.query(record));
  }

  @Override
  public List<UserModel> getByProviderAndName(String provider, String name) {
    return UserModelConvertor.toModelList(userModelDOMapper.getByProviderAndName(provider, name));
  }

  @Override
  public List<UserModel> selectUserPrivateOrPublic(String userNo) {
    return UserModelConvertor.toModelList(userModelDOMapper.selectUserPrivateOrPublic(userNo));
  }

  @Override
  public Long updateBaseInfoByIds(
      List<Long> ids, String name, String visibility, String userNo, String config) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0L;
    }
    return userModelDOMapper.updateBaseInfoByIds(ids, name, visibility, userNo, config);
  }

  @Override
  public JSONObject getByModelId(String modelId) {
    if (StringUtils.isBlank(modelId)) {
      return null;
    }
    String[] split = modelId.split("@");
    if (split.length == 2) {
      String instanceId = split[0];
      UserModel userModel = getByInstanceId(instanceId);
      if (userModel == null) {
        return null;
      }
      ModelProvider provider = modelProviderRepository.getByProvider(userModel.getProvider());
      if (provider == null) {
        return null;
      }
      JSONArray modelList = userModel.getModelList();
      for (Object o : modelList) {
        JSONObject modelJson = (JSONObject) o;
        if (StringUtils.equals(modelId, modelJson.getString(SpgAppConstant.MODEL_ID))) {
          modelJson.put(SpgAppConstant.LOGO, provider.getLogo());
          modelJson.put(SpgAppConstant.PROVIDER, provider.getProvider());
          modelJson = JsonUtils.flatten(modelJson);
          return modelJson;
        }
      }
    }
    return null;
  }
}
