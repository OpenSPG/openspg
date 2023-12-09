package com.antgroup.openspg.builder.core.normalize;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;

/**
 * 属性标准化，针对以下情况会执行对应的标准化操作：
 *
 * <ul>
 *   <li>1. 当属性是基础类型时，则对属性进行类型校验及转化到正确类型
 *   <li>2. 当属性是非基础类型时，则对属性进行属性挂载
 * </ul>
 */
public interface PropertyNormalizer {
  /** 初始化属性标准策略 */
  void init(BuilderContext context) throws BuilderException;

  /** 输入一条spg属性记录，对该属性进行标准化 */
  void propertyNormalize(SPGPropertyRecord record) throws PropertyNormalizeException;
}
