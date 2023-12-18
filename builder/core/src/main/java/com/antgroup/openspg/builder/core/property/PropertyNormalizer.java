package com.antgroup.openspg.builder.core.property;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;

public interface PropertyNormalizer {

  /** Initialize property normalization strategy. */
  void init(BuilderContext context) throws BuilderException;

  /** Input an SPG property record and normalize the property value. */
  void propertyNormalize(BasePropertyRecord record) throws PropertyNormalizeException;
}
