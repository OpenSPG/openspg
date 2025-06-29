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

import com.antgroup.openspg.server.common.model.providerparam.ProviderParam;
import java.util.List;

public interface ProviderParamManager {

  /** insert provider param */
  Long insert(ProviderParam record);

  /** delete by id */
  int deleteById(Long id);

  /** update provider param */
  Long update(ProviderParam record);

  /** get by id */
  ProviderParam getById(Long id);

  /** query provider param */
  List<ProviderParam> query(String provider, String modelType);

  /** get by provider and model type */
  ProviderParam getByProviderAndModelType(String provider, String modelType);
}
