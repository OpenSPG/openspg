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

import com.antgroup.openspg.server.common.model.provider.ModelProvider;
import com.antgroup.openspg.server.common.model.provider.ModelProviderQuery;
import com.antgroup.openspg.server.common.service.provider.ModelProviderRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.ModelProviderDO;
import com.antgroup.openspg.server.infra.dao.mapper.ModelProviderDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.ModelProviderConvertor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ModelProviderRepositoryImpl implements ModelProviderRepository {

  @Autowired private ModelProviderDOMapper modelProviderDOMapper;

  @Override
  public Long insert(ModelProvider record) {
    ModelProviderDO modelProviderDO = ModelProviderConvertor.toDO(record);
    modelProviderDOMapper.insert(modelProviderDO);
    record.setId(modelProviderDO.getId());
    return record.getId();
  }

  @Override
  public int deleteById(Long id) {
    return modelProviderDOMapper.deleteById(id);
  }

  @Override
  public Long update(ModelProvider record) {
    return modelProviderDOMapper.update(ModelProviderConvertor.toDO(record));
  }

  @Override
  public ModelProvider getById(Long id) {
    return ModelProviderConvertor.toModel(modelProviderDOMapper.getById(id));
  }

  @Override
  public List<ModelProvider> query(ModelProviderQuery record) {
    return ModelProviderConvertor.toModelList(modelProviderDOMapper.query(record));
  }

  @Override
  public ModelProvider getByProvider(String provider) {
    return ModelProviderConvertor.toModel(modelProviderDOMapper.getByProvider(provider));
  }

  @Override
  public List<ModelProvider> selectByProviders(List<String> providerList) {
    return ModelProviderConvertor.toModelList(
        modelProviderDOMapper.selectByProviders(providerList));
  }
}
