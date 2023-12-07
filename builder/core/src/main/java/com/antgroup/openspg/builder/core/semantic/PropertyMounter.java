package com.antgroup.openspg.builder.core.semantic;

import com.antgroup.openspg.builder.model.BuilderException;
import com.antgroup.openspg.builder.core.runtime.PropertyMounterException;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;

/** 属性挂载将某个非基础类型的属性链接到具体某个实例id上 */
public interface PropertyMounter {

  /** 初始化属性挂载策略 */
  void init(RuntimeContext context) throws BuilderException;

  /** 输出一条spg记录，当该spg记录的某些属性是非基础类型时，原地执行属性挂载 */
  void propertyMount(SPGPropertyRecord record) throws PropertyMounterException;
}
