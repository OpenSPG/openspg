package com.antgroup.openspg.builder.core.semantic.impl;

import com.antgroup.openspg.builder.core.BuilderException;
import com.antgroup.openspg.builder.core.runtime.PropertyMounterException;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.model.pipeline.config.SearchEnginePropertyMounterConfig;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;

public class SearchEnginePropertyMounter implements PropertyMounter {

  private final SearchEnginePropertyMounterConfig mounterConfig;

  public SearchEnginePropertyMounter(SearchEnginePropertyMounterConfig config) {
    this.mounterConfig = config;
  }

  @Override
  public void init(RuntimeContext context) throws BuilderException {}

  @Override
  public void propertyMount(BaseSPGRecord record) throws PropertyMounterException {}
}
