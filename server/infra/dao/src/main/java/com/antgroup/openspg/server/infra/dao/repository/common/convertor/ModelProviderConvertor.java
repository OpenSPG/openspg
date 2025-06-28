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
import com.antgroup.openspg.server.common.model.provider.ModelProvider;
import com.antgroup.openspg.server.infra.dao.dataobject.ModelProviderDO;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class ModelProviderConvertor {

  public static ModelProviderDO toDO(ModelProvider modelProvider) {
    if (modelProvider == null) {
      return null;
    }
    ModelProviderDO modelProviderDO = new ModelProviderDO();
    modelProviderDO.setId(modelProvider.getId());
    modelProviderDO.setGmtCreate(modelProvider.getGmtCreate());
    modelProviderDO.setGmtModified(modelProvider.getGmtModified());
    modelProviderDO.setCreator(modelProvider.getCreator());
    modelProviderDO.setModifier(modelProvider.getModifier());
    modelProviderDO.setName(modelProvider.getName());
    modelProviderDO.setProvider(modelProvider.getProvider());
    modelProviderDO.setPageMode(modelProvider.getPageMode());
    modelProviderDO.setLogo(modelProvider.getLogo());
    modelProviderDO.setTags(modelProvider.getTags());
    if (modelProvider.getParams() != null) {
      String paramsStr = JSON.toJSONString(modelProvider.getParams());
      modelProviderDO.setParams(paramsStr);
    }
    if (CollectionUtils.isNotEmpty(modelProvider.getModelType())) {
      String modelTypeStr = StringUtils.join(modelProvider.getModelType(), ",");
      modelProviderDO.setModelType(modelTypeStr);
    }
    return modelProviderDO;
  }

  public static ModelProvider toModel(ModelProviderDO modelProviderDO) {
    if (modelProviderDO == null) {
      return null;
    }
    ModelProvider modelProvider = new ModelProvider();
    modelProvider.setId(modelProviderDO.getId());
    modelProvider.setGmtCreate(modelProviderDO.getGmtCreate());
    modelProvider.setGmtModified(modelProviderDO.getGmtModified());
    modelProvider.setCreator(modelProviderDO.getCreator());
    modelProvider.setModifier(modelProviderDO.getModifier());
    modelProvider.setName(modelProviderDO.getName());
    modelProvider.setProvider(modelProviderDO.getProvider());
    modelProvider.setPageMode(modelProviderDO.getPageMode());
    modelProvider.setLogo(modelProviderDO.getLogo());
    modelProvider.setTags(modelProviderDO.getTags());
    if (StringUtils.isNotBlank(modelProviderDO.getParams())) {
      JSONArray parseArray = JSON.parseArray(modelProviderDO.getParams());
      modelProvider.setParams(parseArray);
    }
    if (StringUtils.isNotBlank(modelProviderDO.getModelType())) {
      List<String> modelTypeList = Arrays.asList(modelProviderDO.getModelType().split(","));
      modelProvider.setModelType(modelTypeList);
    }
    return modelProvider;
  }

  public static List<ModelProviderDO> toDoList(List<ModelProvider> providers) {
    if (providers == null) {
      return null;
    }
    List<ModelProviderDO> dos = Lists.newArrayList();
    for (ModelProvider provider : providers) {
      dos.add(toDO(provider));
    }
    return dos;
  }

  public static List<ModelProvider> toModelList(List<ModelProviderDO> providerDOs) {
    if (providerDOs == null) {
      return null;
    }
    List<ModelProvider> jobs = Lists.newArrayList();
    for (ModelProviderDO providerDO : providerDOs) {
      jobs.add(toModel(providerDO));
    }
    return jobs;
  }
}
