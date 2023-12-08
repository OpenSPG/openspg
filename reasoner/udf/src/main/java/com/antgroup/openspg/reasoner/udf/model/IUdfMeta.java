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

package com.antgroup.openspg.reasoner.udf.model;

import com.antgroup.openspg.reasoner.common.types.KgType;

public interface IUdfMeta extends IMetaBase {

  /**
   * Getter method for property <tt>resultType</tt>.
   *
   * @return
   */
  KgType getResultType();

  /**
   * invoke udf method
   *
   * @param args
   * @return
   */
  Object invoke(Object... args);

  /**
   * Getter method for property <tt>description</tt>.
   *
   * @return property value of description
   */
  String getDescription();

  /**
   * Getter method for property <tt>udfType</tt>.
   *
   * @return property value of udfType
   */
  UdfOperatorTypeEnum getUdfType();
}
