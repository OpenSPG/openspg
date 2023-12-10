package com.antgroup.openspg.builder.core.property.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest;

public class SearchPropertyNormalizer extends AdvancedPropertyNormalizer {

  private SearchEngineClient searchEngineClient;

  @Override
  public void init(BuilderContext context) throws BuilderException {
    //    searchEngineClient =
    //        SearchEngineClientDriverManager.getClient(context.getSearchEngineConnectionInfo());
  }

  @Override
  public void propertyNormalize(BasePropertyRecord record) throws PropertyNormalizeException {
    SearchRequest request = new SearchRequest();
  }
}
