package com.antgroup.openspg.builder.core.property.impl;

import com.antgroup.openspg.builder.core.property.PropertyNormalizer;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import java.util.Collections;
import java.util.List;

public class PropertyIdEqualsNormalizer implements PropertyNormalizer {

  public static final PropertyIdEqualsNormalizer INSTANCE = new PropertyIdEqualsNormalizer();

  private PropertyIdEqualsNormalizer() {}

  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public void propertyNormalize(BasePropertyRecord record) throws PropertyNormalizeException {
    SPGTypeRef objectTypeRef = record.getObjectTypeRef();
    if (!objectTypeRef.isAdvancedType()) {
      throw new IllegalStateException();
    }

    List<String> rawValues = record.getRawValues();
    record.getValue().setStds(Collections.singletonList(rawValues));
    record.getValue().setIds(rawValues);
  }
}
