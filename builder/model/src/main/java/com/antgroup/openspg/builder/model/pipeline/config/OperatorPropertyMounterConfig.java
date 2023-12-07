package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyMounterTypeEnum;
import java.util.Map;
import lombok.Getter;

@Getter
public class OperatorPropertyMounterConfig extends PropertyMounterConfig {

  private final OperatorConfig config;

  private final Map<String, String> params;

  public OperatorPropertyMounterConfig(OperatorConfig config, Map<String, String> params) {
    super(PropertyMounterTypeEnum.OPERATOR);
    this.config = config;
    this.params = params;
  }
}
