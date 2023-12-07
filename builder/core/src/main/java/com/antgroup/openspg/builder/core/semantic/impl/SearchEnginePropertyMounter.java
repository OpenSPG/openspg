package com.antgroup.openspg.builder.core.semantic.impl;

import com.antgroup.openspg.builder.model.BuilderException;
import com.antgroup.openspg.builder.core.runtime.PropertyMounterException;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.model.pipeline.config.SearchEnginePropertyMounterConfig;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;

public class SearchEnginePropertyMounter implements PropertyMounter {

  private final SearchEnginePropertyMounterConfig mounterConfig;

  public SearchEnginePropertyMounter(SearchEnginePropertyMounterConfig config) {
    this.mounterConfig = config;
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public void propertyMount(SPGPropertyRecord record) throws PropertyMounterException {
  }
}
