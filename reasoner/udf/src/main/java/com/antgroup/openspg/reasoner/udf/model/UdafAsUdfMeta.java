/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.udf.model;

import com.antgroup.openspg.reasoner.common.types.KTList;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.google.common.collect.Lists;
import java.util.List;

public class UdafAsUdfMeta extends UdafMeta implements IUdfMeta {
  /**
   * udaf meta information
   *
   * @param name
   * @param compatibleName
   * @param description
   * @param udfType
   * @param aggregateClass
   */
  public UdafAsUdfMeta(
      String name,
      String compatibleName,
      String description,
      UdfOperatorTypeEnum udfType,
      Class<? extends BaseUdaf> aggregateClass) {
    super(name, compatibleName, description, udfType, aggregateClass);
  }

  @Override
  public List<KgType> getParamTypeList() {
    return Lists.newArrayList(new KTList(getRowDataType()));
  }

  @Override
  public Object invoke(Object... args) {
    BaseUdaf baseUdaf = this.createAggregateFunction();
    Object[] initParams = new Object[0];
    if (args.length > 1) {
      initParams = new Object[args.length - 1];
      System.arraycopy(args, 1, initParams, 0, args.length - 1);
    }
    baseUdaf.initialize(initParams);
    List<Object> aggList = (List<Object>) args[0];
    for (Object obj : aggList) {
      baseUdaf.update(obj);
    }
    return baseUdaf.evaluate();
  }

  @Override
  public UdfOperatorTypeEnum getUdfType() {
    return this.udfType;
  }
}
