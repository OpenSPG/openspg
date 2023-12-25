package com.antgroup.openspg.builder.core.strategy.fusing.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.strategy.fusing.EntityFusing;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import java.util.List;

public class NewInstanceFusing implements EntityFusing {

  public static final NewInstanceFusing INSTANCE = new NewInstanceFusing();

  private NewInstanceFusing() {}

  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public List<BaseAdvancedRecord> fusing(List<BaseAdvancedRecord> records)
      throws FusingException {
    return records;
  }
}
