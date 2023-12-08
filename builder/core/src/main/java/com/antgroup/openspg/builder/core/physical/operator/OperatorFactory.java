package com.antgroup.openspg.builder.core.physical.operator;

import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.core.schema.model.type.OperatorKey;

public interface OperatorFactory {

  void init(BuilderContext context) throws BuilderException;

  boolean register(OperatorConfig config);

  Object invoke(OperatorKey key, Object... input);
}
