package com.antgroup.openspg.builder.core.operator.linking.impl;

import com.antgroup.openspg.builder.core.operator.linking.PropertyLinking;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import java.util.List;

public class IdEqualsLinking implements PropertyLinking {

  public static final IdEqualsLinking INSTANCE = new IdEqualsLinking();

  private IdEqualsLinking() {}

  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public void propertyLinking(BasePropertyRecord record) throws LinkingException {
    SPGTypeRef objectTypeRef = record.getObjectTypeRef();
    if (!objectTypeRef.isAdvancedType()) {
      throw new IllegalStateException();
    }

    List<String> rawValues = record.getRawValues();
    record.getValue().setStrStds(rawValues);
    record.getValue().setIds(rawValues);
  }
}
