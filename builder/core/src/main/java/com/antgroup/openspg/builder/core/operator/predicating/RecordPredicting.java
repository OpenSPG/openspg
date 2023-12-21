package com.antgroup.openspg.builder.core.operator.predicating;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PredictingException;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;

public interface RecordPredicting {

  void init(BuilderContext context) throws BuilderException;

  void propertyPredicating(BaseSPGRecord spgRecord) throws PredictingException;
}
