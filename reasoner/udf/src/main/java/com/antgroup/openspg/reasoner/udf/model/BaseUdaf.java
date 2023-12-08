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

import com.antgroup.openspg.reasoner.common.types.KgType;
import java.io.Serializable;

public interface BaseUdaf extends Serializable {

  /**
   * udaf input row data types
   *
   * @return
   */
  KgType getInputRowType();

  /**
   * udaf result type
   *
   * @return
   */
  KgType getResultType();

  /**
   * initialize udaf
   *
   * @param params
   */
  void initialize(Object... params);

  /**
   * update row
   *
   * @param row
   */
  void update(Object row);

  /**
   * merge udaf
   *
   * @param function
   */
  void merge(BaseUdaf function);

  /**
   * get udaf result
   *
   * @return
   */
  Object evaluate();
}
