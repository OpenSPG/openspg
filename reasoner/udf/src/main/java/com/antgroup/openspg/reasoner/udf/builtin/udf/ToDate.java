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
import com.antgroup.openspg.reasoner.udf.utils.DateUtils;

public class ToDate {
  @UdfDefine(name = "to_date", compatibleName = "ToDate")
  public String toDate(String dateStr) {
    String df2 = "yyyy-MM-dd";
    return toDate(dateStr, df2);
  }

  @UdfDefine(name = "to_date", compatibleName = "ToDate")
  public String toDate(String dateStr, String toFormat) {
    String data = String.valueOf(dateStr);
    try {
      long sec = DateUtils.parseDateFromStr(data).getTime() / 1000;
      return DateUtils.second2Str(sec, toFormat);
    } catch (Exception ex) {
      return null;
    }
  }
}
