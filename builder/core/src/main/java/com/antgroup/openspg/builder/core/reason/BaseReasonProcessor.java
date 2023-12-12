package com.antgroup.openspg.builder.core.reason;

import com.antgroup.openspg.builder.core.physical.BasePhysicalNode;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;

public abstract class BaseReasonProcessor extends BasePhysicalNode {

  public BaseReasonProcessor(
      String id, String name, BuilderContext context, boolean isInitialized) {
    super(id, name, context, isInitialized);
  }
}
