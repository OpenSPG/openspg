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
import com.alibaba.fastjson.JSONArray;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.usermodel.UserModel;
import com.antgroup.openspg.server.infra.dao.dataobject.UserModelDO;
import com.google.common.collect.Lists;
import java.util.List;

public class UserModelConvertor {

  public static UserModelDO toDO(UserModel userModel) {
    if (null == userModel) {
      return null;
    }
    UserModelDO userModelDO = new UserModelDO();
    userModelDO.setId(userModel.getId());
    userModelDO.setGmtCreate(userModel.getGmtCreate());
    userModelDO.setGmtModified(userModel.getGmtModified());
    userModelDO.setModifier(userModel.getModifier());
    userModelDO.setCreator(userModel.getUserNo());
    userModelDO.setProvider(userModel.getProvider());
    userModelDO.setName(userModel.getName());
    userModelDO.setInstanceId(userModel.getInstanceId());
    userModelDO.setVisibility(userModel.getVisibility());
    userModelDO.setConfig(userModel.getConfig());
    return userModelDO;
  }

  public static UserModel toModel(UserModelDO userModelDO) {
    if (null == userModelDO) {
      return null;
    }
    UserModel userModel = new UserModel();
    userModel.setId(userModelDO.getId());
    userModel.setGmtCreate(userModelDO.getGmtCreate());
    userModel.setGmtModified(userModelDO.getGmtModified());
    userModel.setModifier(userModelDO.getModifier());
    userModel.setUserNo(userModelDO.getCreator());
    userModel.setProvider(userModelDO.getProvider());
    userModel.setName(userModelDO.getName());
    userModel.setInstanceId(userModelDO.getInstanceId());
    userModel.setVisibility(userModelDO.getVisibility());
    if (StringUtils.isNotBlank(userModelDO.getConfig())) {
      JSONArray jsonArray = JSON.parseArray(userModelDO.getConfig());
      userModel.setModelList(jsonArray);
    }
    return userModel;
  }

  public static List<UserModelDO> toDoList(List<UserModel> params) {
    if (params == null) {
      return null;
    }
    List<UserModelDO> dos = Lists.newArrayList();
    for (UserModel provider : params) {
      dos.add(toDO(provider));
    }
    return dos;
  }

  public static List<UserModel> toModelList(List<UserModelDO> paramDOs) {
    if (paramDOs == null) {
      return null;
    }
    List<UserModel> jobs = Lists.newArrayList();
    for (UserModelDO providerDO : paramDOs) {
      jobs.add(toModel(providerDO));
    }
    return jobs;
  }
}
