package com.antgroup.openspg.builder.core.runtime;

import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.BuilderRecordException;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.List;

/**
 * According to the physical execution plan, input a batch of data and output a batch of data after
 * processing is complete. The physical execution plan mentioned here does not include the source
 * and sink of the records.
 */
public interface BuilderExecutor {
  /**
   * Initialize the executor and initialize the physical execution plan. If an exception is thrown,
   * the builder process will not proceed further.
   */
  void init(PhysicalPlan plan, BuilderContext context) throws BuilderException;

  /**
   * Input a batch of records, execute the physical plan, and return the computational results. If
   * an exception is thrown, it signifies a processing failure of this batch of data, and the
   * exception will be handled by the runner.
   */
  List<BaseRecord> eval(List<BaseRecord> inputRecords) throws BuilderRecordException;
}
