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

import com.antgroup.openspg.server.biz.common.ModelProviderManager;
import com.antgroup.openspg.server.common.model.provider.ModelProvider;
import com.antgroup.openspg.server.common.model.provider.ModelProviderQuery;
import com.antgroup.openspg.server.common.service.provider.ModelProviderRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModelProviderManagerImpl implements ModelProviderManager {

  @Autowired private ModelProviderRepository modelProviderRepository;

  @Override
  public Long insert(ModelProvider record) {
    return modelProviderRepository.insert(record);
  }

  @Override
  public int deleteById(Long id) {
    return modelProviderRepository.deleteById(id);
  }

  @Override
  public Long update(ModelProvider record) {
    return modelProviderRepository.update(record);
  }

  @Override
  public ModelProvider getById(Long id) {
    return modelProviderRepository.getById(id);
  }

  @Override
  public ModelProvider getByProvider(String provider) {
    return modelProviderRepository.getByProvider(provider);
  }

  @Override
  public List<ModelProvider> query(String modelType) {
    ModelProviderQuery record = new ModelProviderQuery();
    record.setModelType(modelType);
    record.setOrder("asc");
    record.setSort("id");
    return modelProviderRepository.query(record);
  }

  @Override
  public List<ModelProvider> selectByProviders(List<String> providerList) {
    return modelProviderRepository.selectByProviders(providerList);
  }
}
