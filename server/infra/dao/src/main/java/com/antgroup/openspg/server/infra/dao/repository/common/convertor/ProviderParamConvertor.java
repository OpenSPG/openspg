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
import com.antgroup.openspg.server.common.model.providerparam.ProviderParam;
import com.antgroup.openspg.server.infra.dao.dataobject.ProviderParamDO;
import com.google.common.collect.Lists;
import java.util.List;

public class ProviderParamConvertor {

  public static ProviderParamDO toDO(ProviderParam providerParam) {
    if (null == providerParam) {
      return null;
    }
    ProviderParamDO providerParamDO = new ProviderParamDO();
    providerParamDO.setId(providerParam.getId());
    providerParamDO.setGmtCreate(providerParam.getGmtCreate());
    providerParamDO.setGmtModified(providerParam.getGmtModified());
    providerParamDO.setModifier(providerParam.getModifier());
    providerParamDO.setCreator(providerParam.getCreator());
    providerParamDO.setProvider(providerParam.getProvider());
    providerParamDO.setModelType(providerParam.getModelType());
    providerParamDO.setModelName(providerParam.getModelName());
    if (providerParam.getParams() != null) {
      String paramsStr = JSON.toJSONString(providerParam.getParams());
      providerParamDO.setParams(paramsStr);
    }
    return providerParamDO;
  }

  public static ProviderParam toModel(ProviderParamDO providerParamDO) {
    if (null == providerParamDO) {
      return null;
    }
    ProviderParam providerParam = new ProviderParam();
    providerParam.setId(providerParamDO.getId());
    providerParam.setGmtCreate(providerParamDO.getGmtCreate());
    providerParam.setGmtModified(providerParamDO.getGmtModified());
    providerParam.setModifier(providerParamDO.getModifier());
    providerParam.setCreator(providerParamDO.getCreator());
    providerParam.setProvider(providerParamDO.getProvider());
    providerParam.setModelType(providerParamDO.getModelType());
    providerParam.setModelName(providerParamDO.getModelName());
    if (StringUtils.isNotBlank(providerParamDO.getParams())) {
      JSONArray JSONArray = JSON.parseArray(providerParamDO.getParams());
      providerParam.setParams(JSONArray);
    }
    return providerParam;
  }

  public static List<ProviderParamDO> toDoList(List<ProviderParam> params) {
    if (params == null) {
      return null;
    }
    List<ProviderParamDO> dos = Lists.newArrayList();
    for (ProviderParam provider : params) {
      dos.add(toDO(provider));
    }
    return dos;
  }

  public static List<ProviderParam> toModelList(List<ProviderParamDO> paramDOs) {
    if (paramDOs == null) {
      return null;
    }
    List<ProviderParam> jobs = Lists.newArrayList();
    for (ProviderParamDO providerDO : paramDOs) {
      jobs.add(toModel(providerDO));
    }
    return jobs;
  }
}
