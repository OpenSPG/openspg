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

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;

public class VectorSearchQuery extends BaseQuery {

  @Getter private final String label;
  @Getter private final String propertyKey;
  @Getter private final float[] queryVector;
  @Getter private final int efSearch;

  public VectorSearchQuery(
      @Nullable String label, @NonNull String propertyKey, float @NonNull [] queryVector) {
    this(label, propertyKey, queryVector, -1);
  }

  public VectorSearchQuery(
      @Nullable String label,
      @NonNull String propertyKey,
      float @NonNull [] queryVector,
      int efSearch) {
    this.label = label;
    this.propertyKey = propertyKey;
    this.queryVector = queryVector;
    this.efSearch = efSearch;
  }
}
