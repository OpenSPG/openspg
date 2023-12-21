package com.antgroup.openspg.builder.core.operator.linking;

import com.antgroup.openspg.builder.core.operator.linking.impl.IdEqualsLinking;
import com.antgroup.openspg.builder.core.operator.linking.impl.OperatorLinking;
import com.antgroup.openspg.builder.model.pipeline.config.linking.BaseLinkingConfig;
import com.antgroup.openspg.builder.model.pipeline.config.linking.OperatorLinkingConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.LinkingTypeEnum;

public class PropertyLinkingFactory {

  public static PropertyLinking getPropertyLinking(BaseLinkingConfig config) {
    LinkingTypeEnum linkingType = config.getLinkingType();
    switch (linkingType) {
      case OPERATOR:
        return new OperatorLinking((OperatorLinkingConfig) config);
      case ID_EQUALS:
        return IdEqualsLinking.INSTANCE;
      default:
        throw new IllegalArgumentException("illegal linking type=" + linkingType);
    }
  }
}
