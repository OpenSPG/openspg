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

public class DateAdd {

  @UdfDefine(name = "date_add", compatibleName = "DateAdd,DtAdd")
  public String dateAdd(String date1, Integer addNum, String datePart) {
    Calendar calendar = Calendar.getInstance();
    String dateStr = date1;
    SimpleDateFormat useFormat = DateUtils.getSimpleDateFormat(dateStr);
    calendar.setTime(DateUtils.parseDateFromStr(dateStr));

    int datePartType;
    if ("mm".equals(datePart)) {
      datePartType = Calendar.MONTH;
    } else if ("yyyy".equals(datePart)) {
      datePartType = Calendar.YEAR;
    } else if ("dd".equals(datePart)) {
      datePartType = Calendar.DAY_OF_YEAR;
    } else if ("hh".equals(datePart)) {
      datePartType = Calendar.HOUR;
    } else if ("mi".equals(datePart)) {
      datePartType = Calendar.MINUTE;
    } else if ("ss".equals(datePart)) {
      datePartType = Calendar.SECOND;
    } else if ("year".equals(datePart)) {
      datePartType = Calendar.YEAR;
    } else if ("month".equals(datePart)) {
      datePartType = Calendar.MONTH;
    } else if ("day".equals(datePart)) {
      datePartType = Calendar.DAY_OF_YEAR;
    } else if ("hour".equals(datePart)) {
      datePartType = Calendar.HOUR;
    } else if ("minute".equals(datePart)) {
      datePartType = Calendar.MINUTE;
    } else if ("second".equals(datePart)) {
      datePartType = Calendar.SECOND;
    } else {
      throw new RuntimeException("unsupported date part type");
    }
    calendar.add(datePartType, addNum);
    return useFormat.format(calendar.getTime());
  }

  @UdfDefine(name = "date_add", compatibleName = "DateAdd,DtAdd")
  public String dateAdd(String date1, String date2, String datePart) {
    int addNum = Integer.parseInt(date2);
    return dateAdd(date1, addNum, datePart);
  }
}
