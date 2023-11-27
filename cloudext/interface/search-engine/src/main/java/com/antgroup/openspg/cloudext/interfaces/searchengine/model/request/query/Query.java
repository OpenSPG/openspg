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

package com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query;

/**
 * This is the base class for conditional searches. It is the most fundamental query unit that
 * defines the field to be retrieved and their corresponding value.
 *
 * <p>MatchQuery, TermQuery, FuzzyQuery, and others all inherit from Query, and their query assembly
 * formats are based on key-value pairs, which are relatively simple.
 */
public class Query extends BaseQuery {

  private String name;
  private Object value;

  public Query(String name, Object value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
  }
}
