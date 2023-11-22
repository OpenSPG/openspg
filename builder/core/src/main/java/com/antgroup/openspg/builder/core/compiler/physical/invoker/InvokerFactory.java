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

package com.antgroup.openspg.builder.core.compiler.physical.invoker;

import com.antgroup.openspg.builder.core.pipeline.config.OperatorConfig;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.protocol.BaseRecord;
import java.util.List;

public interface InvokerFactory {

  void init(RuntimeContext context);

  void register(OperatorConfig operatorConfig);

  List<BaseRecord> invoke(InvokerParam param);
}
