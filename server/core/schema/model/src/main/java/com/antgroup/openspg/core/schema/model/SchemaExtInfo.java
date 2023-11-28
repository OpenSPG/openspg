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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.core.schema.model;

import java.util.HashMap;

/**
 * Ontology extension information, used to record additional information on spg types, properties,
 * or relations.
 */
public class SchemaExtInfo extends HashMap<String, Object> {

  private static final long serialVersionUID = -897786189244210137L;

  public Boolean getBoolean(String key) {
    return containsKey(key) ? (Boolean) (get(key)) : false;
  }

  public String getString(String key) {
    return containsKey(key) ? (String) get(key) : null;
  }

  public Long getLong(String key) {
    return containsKey(key) ? (Long) get(key) : null;
  }
}
