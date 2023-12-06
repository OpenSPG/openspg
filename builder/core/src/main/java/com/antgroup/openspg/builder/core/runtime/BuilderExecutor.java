package com.antgroup.openspg.builder.core.runtime;

import com.antgroup.openspg.builder.core.BuilderException;
import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.List;

/** 知识构建executor接口，提供一个默认实现，主要负责初始化物理计划并按批执行 */
public interface BuilderExecutor {

  /** 初始化物理执行计划，如果报错则不再进行构建流程 */
  void init(PhysicalPlan plan, RuntimeContext context) throws BuilderException;

  /** 输入一批record，执行物理计划并返回计算的结果，如果抛出异常则表示这一批数据构建失败，由runner处理 */
  List<BaseRecord> eval(List<BaseRecord> inputRecords) throws BuilderRecordException;
}
