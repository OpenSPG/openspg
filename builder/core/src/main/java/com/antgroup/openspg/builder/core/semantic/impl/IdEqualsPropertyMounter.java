package com.antgroup.openspg.builder.core.semantic.impl;

import com.antgroup.openspg.builder.model.BuilderException;
import com.antgroup.openspg.builder.core.runtime.PropertyMounterException;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;

public class IdEqualsPropertyMounter implements PropertyMounter {

  public static final IdEqualsPropertyMounter INSTANCE = new IdEqualsPropertyMounter();

  private IdEqualsPropertyMounter() {}

  @Override
  public void init(RuntimeContext context) throws BuilderException {}

  @Override
  public void propertyMount(SPGPropertyRecord record) throws PropertyMounterException {}
}