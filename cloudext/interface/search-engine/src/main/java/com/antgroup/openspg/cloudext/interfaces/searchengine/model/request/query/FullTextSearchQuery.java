package com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query;

import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;

public class FullTextSearchQuery extends BaseQuery {

  @Getter private final String queryString;
  @Getter private final List<String> labelConstraints;

  public FullTextSearchQuery(@NonNull String queryString) {
    this(queryString, null);
  }

  public FullTextSearchQuery(@NonNull String queryString, @Nullable List<String> labelConstraints) {
    this.queryString = queryString;
    this.labelConstraints = labelConstraints;
  }
}
