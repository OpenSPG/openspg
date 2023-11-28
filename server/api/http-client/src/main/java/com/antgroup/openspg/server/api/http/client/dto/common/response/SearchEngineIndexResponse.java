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

package com.antgroup.openspg.server.api.http.client.dto.common.response;

import com.antgroup.openspg.common.model.base.BaseResponse;

public class SearchEngineIndexResponse extends BaseResponse {

  private String connInfo;

  private String indexName;

  public String getConnInfo() {
    return connInfo;
  }

  public SearchEngineIndexResponse setConnInfo(String connInfo) {
    this.connInfo = connInfo;
    return this;
  }

  public String getIndexName() {
    return indexName;
  }

  public SearchEngineIndexResponse setIndexName(String indexName) {
    this.indexName = indexName;
    return this;
  }
}
