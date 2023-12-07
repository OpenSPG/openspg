package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.core.schema.model.type.OperatorKey;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class OperatorConfig extends BaseValObj {

  private final String name;

  private final Integer version;

  private final String address;

  private final String mainClass;

  private final Map<String, String> params;

  public OperatorKey toKey() {
    return new OperatorKey(name, version);
  }
}
