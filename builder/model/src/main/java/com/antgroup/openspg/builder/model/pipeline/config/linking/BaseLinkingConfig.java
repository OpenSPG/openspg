package com.antgroup.openspg.builder.model.pipeline.config.linking;

import com.antgroup.openspg.builder.model.pipeline.config.BaseOperatorConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.LinkingTypeEnum;
import com.antgroup.openspg.builder.model.pipeline.enums.OperatorTypeEnum;
import lombok.Getter;

@Getter
public abstract class BaseLinkingConfig extends BaseOperatorConfig {

  private final LinkingTypeEnum linkingType;

  public BaseLinkingConfig(LinkingTypeEnum linkingType) {
    super(OperatorTypeEnum.LINKING);
    this.linkingType = linkingType;
  }
}
