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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.udf.builtin.udf;

import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.antgroup.openspg.reasoner.udf.utils.DateUtils;

public class DateDiff {
  private static final long SECONDS_OF_DAY = 24 * 3600;

  @UdfDefine(name = "date_diff", compatibleName = "DateDiff")
  public long dateDiff(String d1, String d2) {
    String date1 = String.valueOf(d1).trim();
    String date2 = String.valueOf(d2).trim();
    long ts1 = parse2Second(date1);
    long ts2 = parse2Second(date2);
    return (ts1 - ts2) / SECONDS_OF_DAY;
  }

  private long parse2Second(String date) {
    return DateUtils.parseDateFromStr(date).getTime() / 1000;
  }
}
