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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateFormat {
  @UdfDefine(name = "date_format", compatibleName = "DateFormat")
  public String dateFormat(String dateStr, String toFormat) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(DateUtils.parseDateFromStr(dateStr));
    SimpleDateFormat timeFormat3 = new SimpleDateFormat(toFormat, Locale.ENGLISH);
    return timeFormat3.format(calendar.getTime());
  }

  @UdfDefine(name = "date_format", compatibleName = "DateFormat")
  public String dateFormat(String dateStr, String fromFormat, String toFormat) {
    long sec = DateUtils.str2Second(dateStr, fromFormat);
    return DateUtils.second2Str(sec, toFormat);
  }
}
