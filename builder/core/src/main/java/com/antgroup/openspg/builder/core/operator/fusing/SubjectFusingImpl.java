package com.antgroup.openspg.builder.core.operator.fusing;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.pipeline.config.fusing.BaseFusingConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import java.util.List;

public class SubjectFusingImpl implements SubjectFusing {

  private final BaseFusingConfig fusingConfig;

  public SubjectFusingImpl(BaseFusingConfig fusingConfig) {
    this.fusingConfig = fusingConfig;
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public List<BaseSPGRecord> subjectFusing(List<BaseAdvancedRecord> advancedRecords)
      throws FusingException {
    return null;
  }
}
