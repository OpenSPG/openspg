package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.config.LLMBasedExtractNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.List;

public class LLMBasedExtractProcessor extends BaseExtractProcessor<LLMBasedExtractNodeConfig> {

  public LLMBasedExtractProcessor(String id, String name, LLMBasedExtractNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    return null;
  }

  @Override
  public void close() throws Exception {}
}
