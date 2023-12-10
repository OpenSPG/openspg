package com.antgroup.openspg.builder.core.property.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;

public class IdEqualsPropertyNormalizer extends AdvancedPropertyNormalizer {

  public static final IdEqualsPropertyNormalizer INSTANCE = new IdEqualsPropertyNormalizer();

  private IdEqualsPropertyNormalizer() {}

  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public void propertyNormalize(BasePropertyRecord record) throws PropertyNormalizeException {

  }
}
