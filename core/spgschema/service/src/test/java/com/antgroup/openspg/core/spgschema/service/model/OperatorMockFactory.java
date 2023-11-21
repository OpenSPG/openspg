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

package com.antgroup.openspg.core.spgschema.service.model;

import com.antgroup.openspg.core.spgschema.service.type.model.OperatorConfig;
import com.google.common.collect.Lists;
import java.util.List;

public class OperatorMockFactory {

  public static List<OperatorConfig> mockEntityOperatorConfig() {
    return Lists.newArrayList(
        //                new OperatorConfig(new OperatorKey(1L, 1), OperatorTypeEnum.ENTITY_LINK),
        //                new OperatorConfig(new OperatorKey(2L, 1), OperatorTypeEnum.ENTITY_FUSE)
        );
  }

  public static List<OperatorConfig> mockConceptOperatorConfig() {
    return Lists.newArrayList(
        //                new OperatorConfig(new OperatorKey(3L, 1),
        // OperatorTypeEnum.PROPERTY_NORMALIZE),
        //                new OperatorConfig(new OperatorKey(2L, 1), OperatorTypeEnum.ENTITY_FUSE)
        );
  }

  public static List<OperatorConfig> mockEventOperatorConfig() {
    return Lists.newArrayList(
        //                new OperatorConfig(new OperatorKey(1L, 1), OperatorTypeEnum.ENTITY_LINK),
        //                new OperatorConfig(new OperatorKey(2L, 1), OperatorTypeEnum.ENTITY_FUSE),
        //                new OperatorConfig(new OperatorKey(4L, 1),
        // OperatorTypeEnum.KNOWLEDGE_EXTRACT)
        );
  }
}
