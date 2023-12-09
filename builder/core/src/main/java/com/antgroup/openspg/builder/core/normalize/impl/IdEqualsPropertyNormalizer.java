package com.antgroup.openspg.builder.core.normalize.impl;

import com.antgroup.openspg.builder.core.normalize.AdvancedPropertyNormalizer;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;

public class IdEqualsPropertyNormalizer extends AdvancedPropertyNormalizer {

  public static final IdEqualsPropertyNormalizer INSTANCE = new IdEqualsPropertyNormalizer();

  private IdEqualsPropertyNormalizer() {}

  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public void propertyNormalize(SPGPropertyRecord record) throws PropertyNormalizeException {}
}
