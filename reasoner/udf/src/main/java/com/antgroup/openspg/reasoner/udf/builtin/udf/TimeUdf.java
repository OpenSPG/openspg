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
import java.util.Calendar;

public class TimeUdf {
  @UdfDefine(name = "current_time_millis")
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  @UdfDefine(name = "unix_timestamp")
  public long unixTimestamp() {
    return System.currentTimeMillis() / 1000;
  }

  @UdfDefine(name = "dayOfWeek")
  public int dayOfWeek(String time) {
    long timeMillis = Long.parseLong(time);
    return dayOfWeek(timeMillis);
  }

  @UdfDefine(name = "dayOfWeek")
  public int dayOfWeek(long timeMillis) {
    int dayOfWeek = getCalendar(timeMillis).get(Calendar.DAY_OF_WEEK);
    switch (dayOfWeek) {
      case Calendar.MONDAY:
        return 1;
      case Calendar.TUESDAY:
        return 2;
      case Calendar.WEDNESDAY:
        return 3;
      case Calendar.THURSDAY:
        return 4;
      case Calendar.FRIDAY:
        return 5;
      case Calendar.SATURDAY:
        return 6;
      case Calendar.SUNDAY:
        return 7;
      default:
        return 0;
    }
  }

  @UdfDefine(name = "hourOfDay")
  public int hourOfDay(String time) {
    long timeMillis = Long.parseLong(time);
    return hourOfDay(timeMillis);
  }

  @UdfDefine(name = "hourOfDay")
  public int hourOfDay(long timeMillis) {
    return getCalendar(timeMillis).get(Calendar.HOUR_OF_DAY);
  }

  private Calendar getCalendar(long timeMillis) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timeMillis);
    return calendar;
  }
}
