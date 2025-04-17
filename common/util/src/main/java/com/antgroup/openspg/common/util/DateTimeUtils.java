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

package com.antgroup.openspg.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

  public static final String YYYY_MM_DD1 = "yyyyMMdd";
  public static final String YYYY_MM_DD2 = "yyyy-MM-dd";

  public static final String YYYY_MM_DD_HH_MM1 = "yyyyMMddHHmm";
  public static final String YYYY_MM_DD_HH_MM2 = "yyyy-MM-dd HH:mm";

  public static final String YYYY_MM_DD_HH_MM_SS1 = "yyyy-MM-dd HH:mm:ss";
  public static final String YYYY_MM_DD_HH_MM_SS2 = "yyyyMMddHHmmss";

  public static String bizDateByNow() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD1);
    return simpleDateFormat.format(new Date());
  }

  /** Date to String by yyyy-MM-dd HH:mm:ss */
  public static String getDate2LongStr(Date date) {
    return getDate2Str(YYYY_MM_DD_HH_MM_SS1, date);
  }

  /** Date to String by format */
  public static String getDate2Str(String format, Date date) {
    if (date == null) {
      return "";
    }
    return new SimpleDateFormat(format).format(date);
  }

  public static Date getTime(int past, boolean zeroHMS) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
    if (zeroHMS) {
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
    }
    return calendar.getTime();
  }
}
