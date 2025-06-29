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
package com.antgroup.openspg.server.biz.common.impl;

import com.antgroup.openspg.server.biz.common.ModelDetailManager;
import com.antgroup.openspg.server.biz.common.ProviderParamManager;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailDTO;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailQuery;
import com.antgroup.openspg.server.common.model.providerparam.ProviderParam;
import com.antgroup.openspg.server.common.model.providerparam.ProviderParamQuery;
import com.antgroup.openspg.server.common.service.providerparam.ProviderParamRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviderParamManagerImpl implements ProviderParamManager {

  @Autowired private ProviderParamRepository providerParamRepository;
  @Autowired private ModelDetailManager modelDetailManager;

  @Override
  public Long insert(ProviderParam record) {
    return providerParamRepository.insert(record);
  }

  @Override
  public int deleteById(Long id) {
    return providerParamRepository.deleteById(id);
  }

  @Override
  public Long update(ProviderParam record) {
    return providerParamRepository.update(record);
  }

  @Override
  public ProviderParam getById(Long id) {
    return providerParamRepository.getById(id);
  }

  @Override
  public List<ProviderParam> query(String provider, String modelType) {
    ProviderParamQuery record = new ProviderParamQuery();
    record.setProvider(provider);
    record.setModelType(modelType);
    return providerParamRepository.query(record);
  }

  @Override
  public ProviderParam getByProviderAndModelType(String provider, String modelType) {
    ProviderParam providerParam =
        providerParamRepository.getByProviderAndModelType(provider, modelType);
    if (providerParam == null) {
      return null;
    }
    ModelDetailQuery modelDetailQuery = new ModelDetailQuery();
    modelDetailQuery.setProvider(provider);
    modelDetailQuery.setType(modelType);
    List<ModelDetailDTO> modelList = modelDetailManager.queryDTO(modelDetailQuery);
    providerParam.setModel(modelList);
    return providerParam;
  }
}
