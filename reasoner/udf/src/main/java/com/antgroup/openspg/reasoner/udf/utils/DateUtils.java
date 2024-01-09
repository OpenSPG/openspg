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

package com.antgroup.openspg.reasoner.udf.utils;

import com.google.common.collect.Lists;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateUtils {
  /** udf time zone */
  public static TimeZone timeZone = TimeZone.getDefault();

  public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DEFAULT_DATETIME_FORMAT2 = "yyyyMMdd HH:mm:ss";

  public static final String SIMPLE_DATE_FORMAT = "yyyyMMdd";
  public static final String SIMPLE_DATE_FORMAT2 = "yyyy-MM-dd";

  /** seconds to date string */
  public static String second2Str(long seconds, String format) {
    return millSecond2Str(seconds * 1000L, format);
  }

  public static String millSecond2Str(long millSeconds, String format) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    simpleDateFormat.setTimeZone(timeZone);
    return simpleDateFormat.format(millSeconds);
  }

  public static SimpleDateFormat getSimpleDateFormat(String dateStr) {
    List<String> dateFormats =
        Lists.newArrayList(
            DEFAULT_DATETIME_FORMAT,
            DEFAULT_DATETIME_FORMAT2,
            SIMPLE_DATE_FORMAT,
            SIMPLE_DATE_FORMAT2);
    for (String dateFormat : dateFormats) {
      if (dateFormat.length() != dateStr.length()) {
        continue;
      }
      SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
      sdf.setTimeZone(timeZone);
      return sdf;
    }
    throw new RuntimeException("not support parse default date " + dateStr);
  }

  /** parse date string, support four kind of format */
  public static Date parseDateFromStr(String dateStr) {
    SimpleDateFormat simpleDateFormat = getSimpleDateFormat(dateStr);
    try {
      return simpleDateFormat.parse(dateStr);
    } catch (ParseException e) {
      throw new RuntimeException("date parse error...", e);
    }
  }

  /** convert data string to utc timestamp */
  public static long str2Second(String date, String format) {
    return str2MillSecond(date, format) / 1000L;
  }

  /** convert data string to utc timestamp mill seconds */
  public static long str2MillSecond(String date, String format) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    simpleDateFormat.setTimeZone(timeZone);
    try {
      return simpleDateFormat.parse(date).getTime();
    } catch (ParseException e) {
      throw new RuntimeException("date parse error...", e);
    }
  }
}
