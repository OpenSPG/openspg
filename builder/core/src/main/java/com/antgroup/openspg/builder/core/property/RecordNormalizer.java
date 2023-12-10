package com.antgroup.openspg.builder.core.property;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;

public interface RecordNormalizer {

  void init(BuilderContext context) throws BuilderException;

  void propertyNormalize(BaseSPGRecord spgRecord) throws PropertyNormalizeException;
}
