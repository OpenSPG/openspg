package com.antgroup.openspg.builder.core.strategy.fusing;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.pipeline.config.fusing.BaseFusingConfig;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import java.util.ArrayList;
import java.util.List;

public class SubjectFusingImpl implements SubjectFusing {

  private final EntityFusing entityFusing;

  public SubjectFusingImpl(BaseFusingConfig fusingConfig) {
    this.entityFusing = EntityFusingFactory.getEntityFusing(fusingConfig);
  }

  @Override
  public void init(BuilderContext context) throws BuilderException {
    this.entityFusing.init(context);
  }

  @Override
  public List<BaseAdvancedRecord> fusing(BaseAdvancedRecord advancedRecord) throws FusingException {
    List<BaseAdvancedRecord> records = new ArrayList<>(1);
    records.add(advancedRecord);
    return entityFusing.fusing(records);
  }
}
