package com.antgroup.openspg.builder.model.pipeline.config.linking;

import com.antgroup.openspg.builder.model.pipeline.config.BaseStrategyConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.LinkingTypeEnum;
import com.antgroup.openspg.builder.model.pipeline.enums.StrategyTypeEnum;
import lombok.Getter;

@Getter
public abstract class BaseLinkingConfig extends BaseStrategyConfig {

  private final LinkingTypeEnum linkingType;

  public BaseLinkingConfig(LinkingTypeEnum linkingType) {
    super(StrategyTypeEnum.LINKING);
    this.linkingType = linkingType;
  }
}
