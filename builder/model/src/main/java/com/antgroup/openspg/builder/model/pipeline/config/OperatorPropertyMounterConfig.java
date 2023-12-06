package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.PropertyMounterTypeEnum;
import java.util.Map;
import lombok.Getter;

@Getter
public class OperatorPropertyMounterConfig extends PropertyMounterConfig {

  private final String name;

  private final Integer version;

  private final String address;

  private final Map<String, String> params;

  public OperatorPropertyMounterConfig(
      String name, Integer version, String address, Map<String, String> params) {
    super(PropertyMounterTypeEnum.OPERATOR);
    this.name = name;
    this.version = version;
    this.address = address;
    this.params = params;
  }
}
