package com.antgroup.openspg.builder.core.physical.operator;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;

public interface OperatorFactory {

  void init(BuilderContext context) throws BuilderException;

  void loadOperator(OperatorConfig config);

  Object invoke(OperatorConfig config, Object... input);
}
