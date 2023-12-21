package com.antgroup.openspg.builder.core.operator.fusing.impl;

import com.antgroup.openspg.builder.core.operator.fusing.EntityFusing;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import java.util.List;

public class NewInstanceFusing implements EntityFusing {

  public static final NewInstanceFusing INSTANCE = new NewInstanceFusing();

  private NewInstanceFusing() {}

  @Override
  public void init(BuilderContext context) throws BuilderException {}

  @Override
  public List<BaseSPGRecord> entityFusing(List<BaseSPGRecord> records) throws FusingException {
    return records;
  }
}
