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

import com.antgroup.openspg.server.common.model.providerparam.ProviderParamQuery;
import com.antgroup.openspg.server.infra.dao.dataobject.ProviderParamDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProviderParamDOMapper {

  Long insert(ProviderParamDO record);

  int deleteById(Long id);

  Long update(ProviderParamDO record);

  ProviderParamDO getById(Long id);

  List<ProviderParamDO> query(ProviderParamQuery record);

  ProviderParamDO getByProviderAndModelType(
      @Param("provider") String provider, @Param("modelType") String modelType);
}
