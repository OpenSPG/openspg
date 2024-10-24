package com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query;

import lombok.Getter;
import lombok.NonNull;

public class VectorSearchQuery extends BaseQuery {

  @Getter private final String label;
  @Getter private final String propertyKey;
  @Getter private final float[] queryVector;
  @Getter private final int efSearch;

  public VectorSearchQuery(
      @NonNull String label, @NonNull String propertyKey, float @NonNull [] queryVector) {
    this(label, propertyKey, queryVector, -1);
  }

  public VectorSearchQuery(
      @NonNull String label,
      @NonNull String propertyKey,
      float @NonNull [] queryVector,
      int efSearch) {
    this.label = label;
    this.propertyKey = propertyKey;
    this.queryVector = queryVector;
    this.efSearch = efSearch;
  }
}
