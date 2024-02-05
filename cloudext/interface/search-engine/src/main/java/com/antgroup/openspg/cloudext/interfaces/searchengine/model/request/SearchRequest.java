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

package com.antgroup.openspg.cloudext.interfaces.searchengine.model.request;

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.BaseQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.sort.Sort;
import java.util.List;

public class SearchRequest {

  private String indexName;
  private BaseQuery query;
  private List<Sort> sorts;
  private int from;
  private int size;

  public SearchRequest() {}

  public SearchRequest(String indexName, BaseQuery query, List<Sort> sorts, int from, int size) {
    this.indexName = indexName;
    this.query = query;
    this.sorts = sorts;
    this.from = from;
    this.size = size;
  }

  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public List<Sort> getSorts() {
    return sorts;
  }

  public void setSorts(List<Sort> sorts) {
    this.sorts = sorts;
  }

  public int getFrom() {
    return from;
  }

  public void setFrom(int from) {
    this.from = from;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public BaseQuery getQuery() {
    return query;
  }

  public void setQuery(BaseQuery query) {
    this.query = query;
  }
}
