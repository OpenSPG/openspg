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
package com.antgroup.openspg.server.biz.common;

import com.antgroup.openspg.server.common.model.provider.ModelProvider;
import java.util.List;

public interface ModelProviderManager {

  /** insert model provider */
  Long insert(ModelProvider record);

  /** delete by id */
  int deleteById(Long id);

  /** update model provider */
  Long update(ModelProvider record);

  /** get by id */
  ModelProvider getById(Long id);

  /** get by provider */
  ModelProvider getByProvider(String provider);

  /** query by condition */
  List<ModelProvider> query(String modelType);

  /** get by provider list */
  List<ModelProvider> selectByProviders(List<String> providerList);
}
