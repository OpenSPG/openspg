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

import java.util.List;

/**
 * This is a composite query condition class that can be composed of multiple simple queries. The
 * simple queries can be combined using OR, AND, and NOT operators.
 */
public class QueryGroup extends BaseQuery {

  private List<BaseQuery> queries;
  private OperatorType operator;

  public QueryGroup(List<BaseQuery> queries, OperatorType operator) {
    this.queries = queries;
    this.operator = operator;
  }

  public OperatorType getOperator() {
    return operator;
  }

  public List<BaseQuery> getQueries() {
    return queries;
  }

  public void setQueries(List<BaseQuery> queries) {
    this.queries = queries;
  }
}
