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
import java.text.SimpleDateFormat;
import java.util.Date;

public class FromUnixTime {
  @UdfDefine(name = "from_unix_time", compatibleName = "FromUnixtime")
  public String fromUnixTime(String tsStr) {
    String format = "yyyy-MM-dd HH:mm:ss";
    return fromUnixTime(tsStr, format);
  }

  @UdfDefine(name = "from_unix_time", compatibleName = "FromUnixtime")
  public String fromUnixTime(String tsStr, String format) {
    long ts = Long.parseLong(tsStr);
    return fromUnixTime(ts, format);
  }

  @UdfDefine(name = "from_unix_time", compatibleName = "FromUnixtime")
  public String fromUnixTime(long ts) {
    String format = "yyyy-MM-dd HH:mm:ss";
    return fromUnixTime(ts, format);
  }

  @UdfDefine(name = "from_unix_time", compatibleName = "FromUnixtime")
  public String fromUnixTime(long ts, String format) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    simpleDateFormat.setTimeZone(DateUtils.timeZone);
    return simpleDateFormat.format(new Date(ts * 1000));
  }
}
