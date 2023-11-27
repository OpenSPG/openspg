/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.builder.core.physical.invoker.operator;

import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.server.core.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.server.core.schema.model.type.OperatorKey;

public interface OperatorFactory {

  /**
   * Operator initialization.
   *
   * @param context: Initialize related parameters.
   */
  void init(RuntimeContext context);

  /**
   * Operator registration.
   *
   * @param config: Operator configuration parameters, including operator name, type, version, and
   *     address.
   */
  void register(OperatorConfig config);

  /**
   * Operator invocation.
   *
   * @param key: The unique key of an operator, consisting of its name and version.
   * @param input
   * @return
   */
  Object invoke(OperatorKey key, Object... input);
}
