package com.antgroup.openspg.builder.core.strategy.fusing;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import java.util.List;

public interface EntityFusing {

  /** Initialize entity linking strategy. */
  void init(BuilderContext context) throws BuilderException;

  /** Input a list of SPG records, fuse and return the final result. */
  List<BaseAdvancedRecord> fusing(List<BaseAdvancedRecord> records) throws FusingException;
}
