package com.antgroup.openspg.builder.model.pipeline.config.linking;

import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.LinkingTypeEnum;
import lombok.Getter;

@Getter
public class OperatorLinkingConfig extends BaseLinkingConfig {

  private final OperatorConfig operatorConfig;

  public OperatorLinkingConfig(OperatorConfig operatorConfig) {
    super(LinkingTypeEnum.OPERATOR);
    this.operatorConfig = operatorConfig;
  }
}
