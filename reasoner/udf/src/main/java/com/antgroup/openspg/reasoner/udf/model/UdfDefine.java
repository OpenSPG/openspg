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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UdfDefine {
  /** UDF or UDAF name */
  String name();

  /**
   * UDF type, function or operator
   *
   * @return
   */
  UdfOperatorTypeEnum udfType() default UdfOperatorTypeEnum.FUNCTION;

  /**
   * Compatible with Old UDF or UDAF name
   *
   * @return
   */
  String compatibleName() default "";

  /** description for UDF or UDAF */
  String description() default "";
}
