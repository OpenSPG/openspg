package com.antgroup.openspg.builder.core.strategy.predicting;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PredictingException;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;

public interface RecordPredicting {

  void init(BuilderContext context) throws BuilderException;

  void propertyPredicating(BaseAdvancedRecord advancedRecord) throws PredictingException;
}
