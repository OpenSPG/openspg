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

/** Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved. */
package com.antgroup.openspg.reasoner.udf.utils;

import com.google.common.collect.Lists;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateUtils {

  public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DEFAULT_DATETIME_FORMAT2 = "yyyyMMdd HH:mm:ss";

  public static final String SIMPLE_DATE_FORMAT = "yyyyMMdd";
  public static final String SIMPLE_DATE_FORMAT2 = "yyyy-MM-dd";

  /**
   * 时间转换函数: 秒 -> 字符串. 可以指定字符串时间格式.比如: yyyy-MM-dd等
   *
   * @param seconds 参数: 秒
   * @param format 参数: 字符串时间格式
   * @return 指定格式时间字符串
   */
  public static String second2Str(long seconds, String format) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    return simpleDateFormat.format(seconds * 1000L);
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
      return new SimpleDateFormat(dateFormat);
    }
    throw new RuntimeException("not support parse default date " + dateStr);
  }
  /**
   * 自动将字符串转换成Date格式
   *
   * @param dateStr
   * @return
   */
  public static Date parseDateFromStr(String dateStr) {
    SimpleDateFormat simpleDateFormat = getSimpleDateFormat(dateStr);
    try {
      return simpleDateFormat.parse(dateStr);
    } catch (ParseException e) {
      throw new RuntimeException("date parse error...", e);
    }
  }
  /**
   * 时间转换函数: 字符串 -> 秒. 可以指定字符串时间格式.比如: yyyyMMdd等
   *
   * @param date 参数: 日期，如: 20220201
   * @param format 参数: 字符串时间格式
   * @return 指定格式时间字符串
   */
  public static long str2Second(String date, String format) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    try {
      return simpleDateFormat.parse(date).getTime() / 1000;
    } catch (ParseException e) {
      throw new RuntimeException("date parse error...", e);
    }
  }
}
