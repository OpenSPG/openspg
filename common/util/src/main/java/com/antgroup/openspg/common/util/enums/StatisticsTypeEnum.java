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
package com.antgroup.openspg.common.util.enums;

public enum StatisticsTypeEnum {

  /** USER_STATISTICS */
  USER_STATISTICS,

  /** NORMAL_CHAT_STATISTICS */
  NORMAL_CHAT_STATISTICS,

  /** DEBUG_CHAT_STATISTICS */
  DEBUG_CHAT_STATISTICS,

  /** API_CHAT_STATISTICS */
  API_CHAT_STATISTICS,

  /** CHAT_STATISTICS */
  CHAT_STATISTICS,

  /** TOKENS_STATISTICS */
  TOKENS_STATISTICS,

  /** FEEDBACK_UP_STATISTICS */
  FEEDBACK_UP_STATISTICS,

  /** FEEDBACK_DOWN_STATISTICS */
  FEEDBACK_DOWN_STATISTICS;

  /**
   * get enum by statistics type
   *
   * @param statisticsType
   * @return
   */
  public static StatisticsTypeEnum getStatisticsType(String statisticsType) {
    for (StatisticsTypeEnum tagEnum : StatisticsTypeEnum.values()) {
      if (tagEnum.name().equals(statisticsType)) {
        return tagEnum;
      }
    }

    throw new IllegalArgumentException("Unsupported statistics type:" + statisticsType);
  }
}
