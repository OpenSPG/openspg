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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Closeable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;

/** some common tools */
@Slf4j
public class CommonUtils {

  /** merge two bean by discovering differences */
  public static <M> M merge(M dest, M orig) {
    if (dest == null) {
      return orig;
    }
    if (orig == null) {
      return dest;
    }
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(dest.getClass());
      for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
        if (descriptor.getWriteMethod() == null) {
          continue;
        }
        Object originalValue = descriptor.getReadMethod().invoke(orig);
        if (originalValue == null) {
          continue;
        }
        descriptor.getWriteMethod().invoke(dest, originalValue);
      }
    } catch (Exception e) {
      log.error("merge bean exception", e);
    }
    return dest;
  }

  /** Exception to String */
  public static String getExceptionToString(Throwable e) {
    if (e == null) {
      return "";
    }
    StringWriter stringWriter = null;
    PrintWriter printWriter = null;
    try {
      stringWriter = new StringWriter();
      printWriter = new PrintWriter(stringWriter);
      e.printStackTrace(printWriter);
    } finally {
      close(stringWriter);
      close(printWriter);
    }
    return stringWriter.toString();
  }

  /** close Closeable */
  public static void close(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception e) {
        log.error("Unable to close ", e);
      }
    }
  }

  /** Limit remark */
  public static String setRemarkLimit(String oldRemark, StringBuffer appendRemark) {
    Integer maxLength = 100000;
    return subStringToLength(appendRemark.append(oldRemark), maxLength, "...");
  }

  /** sub String To Length */
  public static String subStringToLength(StringBuffer str, Integer length, String fill) {
    if (str == null) {
      return "";
    }
    if (length == null || length >= str.length()) {
      return str.toString();
    }
    int start = 0;
    fill = (fill == null ? "...":fill);
    return str.substring(start, length - fill.length()) + fill;
  }

  /** get Cron Execution Dates By Today */
  public static List<Date> getCronExecutionDatesByToday(String cron) {
    CronExpression expression = null;
    try {
      expression = new CronExpression(cron);
    } catch (ParseException e) {
      new RuntimeException("Cron ParseException", e);
    }
    List<Date> dates = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    Date today = new Date();
    calendar.setTime(today);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    Date startDate = calendar.getTime();
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    Date endDate = calendar.getTime();

    if (expression.isSatisfiedBy(startDate)) {
      dates.add(startDate);
    }
    Date nextDate = expression.getNextValidTimeAfter(startDate);

    while (nextDate != null && nextDate.before(endDate)) {
      dates.add(nextDate);
      nextDate = expression.getNextValidTimeAfter(nextDate);
    }

    return dates;
  }

  /** get Previous ValidTime */
  public static Date getPreviousValidTime(String cron, Date specifiedTime) {
    CronExpression expression = null;
    try {
      expression = new CronExpression(cron);
    } catch (ParseException e) {
      new RuntimeException("Cron ParseException", e);
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(specifiedTime);

    Date endDate =
        expression.getNextValidTimeAfter(expression.getNextValidTimeAfter(specifiedTime));
    Long time = 2 * specifiedTime.getTime() - endDate.getTime();

    Date start = new Date(time);
    Date nextDate = expression.getNextValidTimeAfter(start);
    Date preDate = nextDate;
    while (nextDate != null && nextDate.before(specifiedTime)) {
      preDate = nextDate;
      nextDate = expression.getNextValidTimeAfter(nextDate);
    }
    return preDate;
  }

  /** get Unique Id */
  public static String getUniqueId(Long jobId, Date schedulerDate) {
    return jobId.toString()
        + DateTimeUtils.getDate2Str(DateTimeUtils.YYYY_MM_DD_HH_MM_SS2, schedulerDate);
  }

  /** content contains key */
  public static boolean contains(String content, String key) {
    if (StringUtils.isBlank(key)) {
      return true;
    }
    if (StringUtils.isBlank(content)) {
      return false;
    }
    if (content.contains(key)) {
      return true;
    }
    return false;
  }

  /** content equals key */
  public static boolean equals(Object content, Object key) {
    if (key == null) {
      return true;
    }
    if (content == null) {
      return false;
    }
    if (content.equals(key)) {
      return true;
    }
    return false;
  }

  /** content Date after key Date */
  public static boolean after(Date content, Date key) {
    if (key == null) {
      return true;
    }
    if (content == null) {
      return false;
    }
    if (content.after(key)) {
      return true;
    }
    return false;
  }

  /** content Date before key Date */
  public static boolean before(Date content, Date key) {
    if (key == null) {
      return true;
    }
    if (content == null) {
      return false;
    }
    if (content.before(key)) {
      return true;
    }
    return false;
  }
}
