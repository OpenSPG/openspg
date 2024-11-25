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

package com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class FullTextSearchQuery extends BaseQuery {

  @Getter private final String queryString;
  @Getter private final List<String> labelConstraints;
  @Getter @Setter
  private Map<String, Object> params;

  public FullTextSearchQuery(@NonNull String queryString) {
    this(queryString, null);
  }

  public FullTextSearchQuery(@NonNull String queryString, @Nullable List<String> labelConstraints) {
    this.queryString = queryString;
    this.labelConstraints = labelConstraints;
  }
}
