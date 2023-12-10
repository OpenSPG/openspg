package com.antgroup.openspg.builder.core.property.impl;

import com.antgroup.openspg.builder.core.property.PropertyNormalizer;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.core.schema.model.type.BasicTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;

public class BasicPropertyNormalizer implements PropertyNormalizer {
  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public boolean propertyNormalize(BasePropertyRecord record) throws PropertyNormalizeException {
    SPGTypeRef objectTypeRef = record.getObjectTypeRef();
    if (!objectTypeRef.isBasicType()) {
      throw new IllegalStateException();
    }

    BasicTypeEnum basicType = BasicTypeEnum.from(objectTypeRef.getName());
    Object stdValue = null;
    String rawValue = record.getValue().getRaw();
    try {
      switch (basicType) {
        case LONG:
          stdValue = Long.valueOf(rawValue);
          break;
        case DOUBLE:
          stdValue = Double.valueOf(rawValue);
          break;
        default:
          stdValue = rawValue;
          break;
      }
    } catch (NumberFormatException e) {
      throw new PropertyNormalizeException(e, "");
    }
    record.getValue().setSingleStd(stdValue);
    return true;
  }
}
