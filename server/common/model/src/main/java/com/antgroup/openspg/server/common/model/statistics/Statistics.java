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
package com.antgroup.openspg.server.common.model.statistics;

import java.util.Date;
import lombok.Data;

@Data
public class Statistics {

  private Long id;
  private Date gmtCreate;
  private Date gmtModified;
  private String modifier;
  private String creator;
  private String resourceTag;
  private String resourceId;
  private String statisticsType;
  private String statisticsDate;
  private Long num;

  public Statistics() {}

  public Statistics(
      Date date,
      String userNo,
      String resourceTag,
      String resourceId,
      String statisticsType,
      String statisticsDate,
      Long num) {
    this.gmtCreate = date;
    this.gmtModified = date;
    this.modifier = userNo;
    this.creator = userNo;
    this.resourceTag = resourceTag;
    this.resourceId = resourceId;
    this.statisticsType = statisticsType;
    this.statisticsDate = statisticsDate;
    this.num = num;
  }
}
