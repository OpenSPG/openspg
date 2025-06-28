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
package com.antgroup.openspg.server.infra.dao.mapper;

import com.antgroup.openspg.server.common.model.provider.ModelProviderQuery;
import com.antgroup.openspg.server.infra.dao.dataobject.ModelProviderDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ModelProviderDOMapper {

  Long insert(ModelProviderDO record);

  int deleteById(Long id);

  Long update(ModelProviderDO record);

  ModelProviderDO getById(Long id);

  ModelProviderDO getByProvider(String provider);

  List<ModelProviderDO> query(ModelProviderQuery record);

  List<ModelProviderDO> selectByProviders(@Param("providerList") List<String> providerList);
}
