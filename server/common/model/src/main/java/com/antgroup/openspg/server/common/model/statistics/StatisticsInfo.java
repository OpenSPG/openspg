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

import lombok.Data;

@Data
public class StatisticsInfo {

  private Long userNum = 0L;
  private Long normalChatNum = 0L;
  private Long debugChatNum = 0L;
  private Long apiChatNum = 0L;
  private Long chatNum = 0L;
  private Long tokens = 0L;
  private Long upNum = 0L;
  private Long downNum = 0L;
  private String statisticsDate;

  public StatisticsInfo() {}

  public StatisticsInfo(String statisticsDate) {
    this.statisticsDate = statisticsDate;
  }

  public StatisticsInfo(
      Long userNum,
      Long normalChatNum,
      Long debugChatNum,
      Long apiChatNum,
      Long chatNum,
      Long tokens,
      Long upNum,
      Long downNum) {
    this.userNum = userNum;
    this.normalChatNum = normalChatNum;
    this.debugChatNum = debugChatNum;
    this.apiChatNum = apiChatNum;
    this.chatNum = chatNum;
    this.tokens = tokens;
    this.upNum = upNum;
    this.downNum = downNum;
  }
}
