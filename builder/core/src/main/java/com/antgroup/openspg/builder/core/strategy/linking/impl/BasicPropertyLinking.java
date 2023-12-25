package com.antgroup.openspg.builder.core.strategy.linking.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.linking.PropertyLinking;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.core.schema.model.type.BasicTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;

public class BasicPropertyLinking implements PropertyLinking {
  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public void linking(BasePropertyRecord record) throws LinkingException {
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
      throw new LinkingException(e, "{} normalize error", rawValue);
    }
    record.getValue().setSingleStd(stdValue);
  }
}
