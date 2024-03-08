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

package com.antgroup.openspg.cloudext.impl.searchengine.elasticsearch.model;

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxField;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.schema.IdxMapping;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EsMapping extends BaseValObj {

  @Getter
  @AllArgsConstructor
  public static class PropertyConfig extends BaseValObj {

    private final String type;
  }

  private final Map<String, PropertyConfig> properties;

  public static EsMapping fromIdxMapping(IdxMapping idxMapping) {
    if (idxMapping == null) {
      return null;
    }

    Map<String, PropertyConfig> properties = new HashMap<>();
    for (IdxField idxField : idxMapping.getIdxFields()) {
      properties.put(
          idxField.getName(), new PropertyConfig(idxField.getBasicType().name().toLowerCase()));
    }
    return new EsMapping(properties);
  }
}
