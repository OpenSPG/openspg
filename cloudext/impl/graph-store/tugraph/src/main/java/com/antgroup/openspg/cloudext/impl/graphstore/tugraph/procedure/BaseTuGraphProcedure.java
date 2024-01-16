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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure;

import com.antgroup.openspg.common.util.StringUtils;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.util.Map;

public abstract class BaseTuGraphProcedure implements TuGraphProcedure {

  /** The cypher template of this procedure */
  private final String cypherTemplate;

  protected BaseTuGraphProcedure(String cypherTemplate) {
    this.cypherTemplate = cypherTemplate;
  }

  /**
   * Export parameters of procedure
   *
   * @return A map which describe the parameters of this procedure.
   */
  protected final Map<String, Object> exportParams() {
    Map<String, Object> paramMap = Maps.newHashMap();
    for (Field field : this.getClass().getDeclaredFields()) {
      String fieldName = field.getName();
      field.setAccessible(true);
      try {
        if (field.get(this) == null) {
          paramMap.put(fieldName, null);
        } else {
          paramMap.put(fieldName, field.get(this).toString());
        }
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return paramMap;
  }

  /**
   * Get cypher template of this procedure.
   *
   * @return the cypher.
   */
  protected String getCypherTemplate() {
    return this.cypherTemplate;
  }

  @Override
  public String getCypher() {
    return StringUtils.dictFormat(exportParams(), getCypherTemplate());
  }
}
