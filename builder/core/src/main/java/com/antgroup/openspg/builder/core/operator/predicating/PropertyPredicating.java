package com.antgroup.openspg.builder.core.operator.predicating;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PredicatingException;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import java.util.List;

public interface PropertyPredicating {

  /** Initialize property linking strategy. */
  void init(BuilderContext context) throws BuilderException;

  /** Input an SPG property record and predicate the property value. */
  List<BaseSPGRecord> propertyPredicating(BaseSPGRecord record) throws PredicatingException;
}
