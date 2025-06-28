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

import com.antgroup.openspg.server.common.model.providerparam.ProviderParam;
import com.antgroup.openspg.server.common.model.providerparam.ProviderParamQuery;
import com.antgroup.openspg.server.common.service.providerparam.ProviderParamRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.ProviderParamDO;
import com.antgroup.openspg.server.infra.dao.mapper.ProviderParamDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.ProviderParamConvertor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderParamRepositoryImpl implements ProviderParamRepository {

  @Autowired private ProviderParamDOMapper paramDOMapper;

  @Override
  public Long insert(ProviderParam record) {
    ProviderParamDO providerParamDO = ProviderParamConvertor.toDO(record);
    paramDOMapper.insert(providerParamDO);
    record.setId(providerParamDO.getId());
    return record.getId();
  }

  @Override
  public int deleteById(Long id) {
    return paramDOMapper.deleteById(id);
  }

  @Override
  public Long update(ProviderParam record) {
    return paramDOMapper.update(ProviderParamConvertor.toDO(record));
  }

  @Override
  public ProviderParam getById(Long id) {
    return ProviderParamConvertor.toModel(paramDOMapper.getById(id));
  }

  @Override
  public List<ProviderParam> query(ProviderParamQuery record) {
    return ProviderParamConvertor.toModelList(paramDOMapper.query(record));
  }

  @Override
  public ProviderParam getByProviderAndModelType(String provider, String modelType) {
    return ProviderParamConvertor.toModel(
        paramDOMapper.getByProviderAndModelType(provider, modelType));
  }
}
