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

package com.antgroup.openspg.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class DateTimeUtils {

  public static final String YYYY_MM_DD1 = "yyyyMMdd";
  public static final String YYYY_MM_DD2 = "yyyy-MM-dd";

  public static final String YYYY_MM_DD_HH_MM1 = "yyyyMMddHHmm";
  public static final String YYYY_MM_DD_HH_MM2 = "yyyy-MM-dd HH:mm";

  public static final String YYYY_MM_DD_HH_MM_SS1 = "yyyy-MM-dd HH:mm:ss";
  public static final String YYYY_MM_DD_HH_MM_SS2 = "yyyyMMddHHmmss";

  public static String bizDateByNow() {
    return getDate2Str(YYYY_MM_DD1, new Date());
  }

  public static String getDate2LongStr(Date date) {
    if (date == null) {
      return "";
    }
    return getDate2Str(YYYY_MM_DD_HH_MM_SS1, date);
  }

  public static String getDate2Str(String format, Date date) {
    if (date == null) {
      return "";
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    return simpleDateFormat.format(date);
  }

  public static Date getStr2Date(String format, String dateStr) {
    if (StringUtils.isBlank(dateStr)) {
      return null;
    }
    Date date = null;
    if (dateStr.length() == format.length()) {
      try {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        date = simpleDateFormat.parse(dateStr);
      } catch (ParseException var4) {
        return null;
      }
    }

    return date;
  }

  public static Date add(Date date, int calendarField, int amount) {
    Validate.notNull(date, "The date must not be null", new Object[0]);
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(calendarField, amount);
    return c.getTime();
  }
}
