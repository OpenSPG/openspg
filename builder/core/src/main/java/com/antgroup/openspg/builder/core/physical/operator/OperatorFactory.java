package com.antgroup.openspg.builder.core.physical.operator;

import com.antgroup.openspg.builder.model.BuilderException;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.core.schema.model.type.OperatorKey;

public interface OperatorFactory {

  void init(RuntimeContext context) throws BuilderException;

  boolean register(OperatorConfig config);

  Object invoke(OperatorKey key, Object... input);
}
