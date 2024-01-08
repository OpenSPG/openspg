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

package com.antgroup.openspg.reasoner.udf.builtin.udf;

import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.antgroup.openspg.reasoner.udf.model.UdfOperatorTypeEnum;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RLike {

  /**
   * regex like match
   *
   * @param inputStr
   * @param regex
   * @return
   */
  @UdfDefine(name = "rlike", udfType = UdfOperatorTypeEnum.OPERATOR)
  public Boolean rlike(String inputStr, String regex) {
    if (inputStr == null) {
      return false;
    }
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(inputStr);
    return matcher.find();
  }
}
