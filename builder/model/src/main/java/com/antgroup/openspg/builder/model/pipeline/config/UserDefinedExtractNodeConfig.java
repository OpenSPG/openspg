package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import lombok.Getter;

@Getter
public class UserDefinedExtractNodeConfig extends BaseExtractNodeConfig {

  public UserDefinedExtractNodeConfig(OperatorConfig operatorConfig) {
    super(NodeTypeEnum.USER_DEFINED_EXTRACT, operatorConfig);
  }
}
