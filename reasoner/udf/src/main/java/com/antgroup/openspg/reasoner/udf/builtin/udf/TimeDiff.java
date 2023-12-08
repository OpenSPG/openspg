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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDiff {
  @UdfDefine(name = "time_diff", compatibleName = "TimeDiff")
  public long timeDiff(String t1, String t2) throws ParseException {
    long time1 = toSeconds(t1);
    long time2 = toSeconds(t2);
    return time1 - time2;
  }

  @UdfDefine(name = "time_diff", compatibleName = "TimeDiff")
  public long timeDiff(Long t1, Long t2) throws ParseException {
    return t1 - t2;
  }

  private long toSeconds(String time) throws ParseException {
    try {
      return Long.valueOf((String) time);
    } catch (NumberFormatException ex) {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date date = df.parse((String) time);
      return date.getTime() / 1000;
    }
  }
}
