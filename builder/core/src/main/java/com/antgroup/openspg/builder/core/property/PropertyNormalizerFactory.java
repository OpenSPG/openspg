package com.antgroup.openspg.builder.core.property;

import com.antgroup.openspg.builder.core.property.impl.PropertyIdEqualsNormalizer;
import com.antgroup.openspg.builder.core.property.impl.PropertyOperatorNormalizer;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorPropertyNormalizerConfig;
import com.antgroup.openspg.builder.model.pipeline.config.PropertyNormalizerConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.PropertyNormalizerTypeEnum;

public class PropertyNormalizerFactory {

  public static PropertyNormalizer getPropertyNormalizer(PropertyNormalizerConfig config) {
    PropertyNormalizerTypeEnum normalizerType = config.getNormalizerType();
    switch (normalizerType) {
      case OPERATOR:
        return new PropertyOperatorNormalizer((OperatorPropertyNormalizerConfig) config);
      case ID_EQUALS:
        return PropertyIdEqualsNormalizer.INSTANCE;
      default:
        throw new IllegalArgumentException("illegal property mounter type=" + normalizerType);
    }
  }
}
