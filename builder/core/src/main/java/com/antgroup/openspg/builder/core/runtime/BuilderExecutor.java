package com.antgroup.openspg.builder.core.runtime;

import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.BuilderRecordException;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.List;

/** 知识构建executor接口，提供一个默认实现，主要负责初始化物理计划并按批执行 */
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
