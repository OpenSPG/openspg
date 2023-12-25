package com.antgroup.openspg.builder.core.strategy.linking;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;

public interface PropertyLinking {

  /** Initialize property linking strategy. */
  void init(BuilderContext context) throws BuilderException;

  /** Input an SPG property record and link the property value. */
  void linking(BasePropertyRecord record) throws LinkingException;
}
