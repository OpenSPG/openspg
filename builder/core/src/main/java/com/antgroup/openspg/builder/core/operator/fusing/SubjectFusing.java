package com.antgroup.openspg.builder.core.operator.fusing;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.FusingException;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.List;

public interface SubjectFusing {

  void init(BuilderContext context) throws BuilderException;

  List<BaseRecord> subjectFusing(List<BaseAdvancedRecord> advancedRecords)
      throws FusingException;
}
