package com.antgroup.openspg.builder.core.semantic.impl;

import com.antgroup.openspg.builder.core.BuilderException;
import com.antgroup.openspg.builder.core.runtime.PropertyMounterException;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorPropertyMounterConfig;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;

public class OperatorPropertyMounter implements PropertyMounter {

  private final OperatorPropertyMounterConfig mounterConfig;

  public OperatorPropertyMounter(OperatorPropertyMounterConfig config) {
    this.mounterConfig = config;
  }

  @Override
  public void init(RuntimeContext context) throws BuilderException {}

  @Override
  public boolean propertyMount(BasePropertyRecord record) throws PropertyMounterException {}
}
