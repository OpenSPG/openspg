package com.antgroup.openspg.builder.core.strategy.linking;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.LinkingException;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;

public interface RecordLinking {

  void init(BuilderContext context) throws BuilderException;

  void linking(BaseSPGRecord spgRecord) throws LinkingException;

  void setDefaultPropertyLinking(PropertyLinking defaultPropertyLinking);
}
