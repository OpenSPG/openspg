/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.reasoner.udf.builtin.udf;

import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import org.apache.commons.lang3.StringUtils;

public class Upper {

  /**
   * Convert the string to uppercase
   *
   * @param str
   * @return
   */
  @UdfDefine(name = "upper", compatibleName = "Upper")
  public String upper(Object str) {
    return StringUtils.upperCase(String.valueOf(str));
  }
}
