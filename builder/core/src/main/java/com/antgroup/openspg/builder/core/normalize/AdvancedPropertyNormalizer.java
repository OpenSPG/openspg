package com.antgroup.openspg.builder.core.normalize;

import com.antgroup.openspg.builder.core.normalize.impl.IdEqualsPropertyNormalizer;
import com.antgroup.openspg.builder.core.normalize.impl.OperatorPropertyNormalizer;
import com.antgroup.openspg.builder.core.normalize.impl.SearchPropertyNormalizer;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorPropertyNormalizerConfig;
import com.antgroup.openspg.builder.model.pipeline.config.PropertyNormalizerConfig;
import com.antgroup.openspg.builder.model.pipeline.config.SearchPropertyNormalizerConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.PropertyNormalizerTypeEnum;

public abstract class AdvancedPropertyNormalizer implements PropertyNormalizer {

  public static AdvancedPropertyNormalizer getPropertyNormalizer(PropertyNormalizerConfig config) {
    PropertyNormalizerTypeEnum normalizerType = config.getNormalizerType();
    switch (normalizerType) {
      case OPERATOR:
        return new OperatorPropertyNormalizer((OperatorPropertyNormalizerConfig) config);
      case SEARCH:
        return new SearchPropertyNormalizer((SearchPropertyNormalizerConfig) config);
      case ID_EQUALS:
        return IdEqualsPropertyNormalizer.INSTANCE;
      default:
        throw new IllegalArgumentException("illegal property mounter type=" + normalizerType);
    }
  }
}
