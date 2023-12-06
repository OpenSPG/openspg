package com.antgroup.openspg.builder.core.physical.operator;

import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.core.schema.model.type.OperatorKey;

public interface OperatorFactory {

  void init(RuntimeContext context) throws Exception;

  boolean register(OperatorConfig config);

  Object invoke(OperatorKey key, Object... input);
}
