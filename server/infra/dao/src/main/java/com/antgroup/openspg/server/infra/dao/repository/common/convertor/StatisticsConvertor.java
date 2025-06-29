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
package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.antgroup.openspg.server.common.model.statistics.Statistics;
import com.antgroup.openspg.server.infra.dao.dataobject.StatisticsDO;
import java.util.List;
import org.apache.commons.compress.utils.Lists;

public class StatisticsConvertor {

  public static StatisticsDO toDO(Statistics statistics) {
    if (statistics == null) {
      return null;
    }
    StatisticsDO statisticsDO = new StatisticsDO();
    statisticsDO.setId(statistics.getId());
    statisticsDO.setGmtCreate(statistics.getGmtCreate());
    statisticsDO.setGmtModified(statistics.getGmtModified());
    statisticsDO.setCreator(statistics.getCreator());
    statisticsDO.setModifier(statistics.getModifier());
    statisticsDO.setResourceTag(statistics.getResourceTag());
    statisticsDO.setResourceId(statistics.getResourceId());
    statisticsDO.setStatisticsType(statistics.getStatisticsType());
    statisticsDO.setStatisticsDate(statistics.getStatisticsDate());
    statisticsDO.setNum(statistics.getNum());
    return statisticsDO;
  }

  public static Statistics toModel(StatisticsDO statisticsDO) {
    if (statisticsDO == null) {
      return null;
    }
    Statistics statistics = new Statistics();
    statistics.setId(statisticsDO.getId());
    statistics.setGmtCreate(statisticsDO.getGmtCreate());
    statistics.setGmtModified(statisticsDO.getGmtModified());
    statistics.setCreator(statisticsDO.getCreator());
    statistics.setModifier(statisticsDO.getModifier());
    statistics.setResourceTag(statisticsDO.getResourceTag());
    statistics.setResourceId(statisticsDO.getResourceId());
    statistics.setStatisticsType(statisticsDO.getStatisticsType());
    statistics.setStatisticsDate(statisticsDO.getStatisticsDate());
    statistics.setNum(statisticsDO.getNum());
    return statistics;
  }

  public static List<Statistics> toModelList(List<StatisticsDO> statisticsDOs) {
    if (statisticsDOs == null) {
      return null;
    }
    List<Statistics> statisticsList = Lists.newArrayList();
    for (StatisticsDO statisticsDO : statisticsDOs) {
      Statistics statistics = toModel(statisticsDO);
      if (statistics != null) {
        statisticsList.add(statistics);
      }
    }
    return statisticsList;
  }

  public static List<StatisticsDO> toDOList(List<Statistics> statisticsList) {
    if (statisticsList == null) {
      return null;
    }
    List<StatisticsDO> statisticsDOs = Lists.newArrayList();
    for (Statistics statistics : statisticsList) {
      StatisticsDO statisticsDO = toDO(statistics);
      if (statisticsDO != null) {
        statisticsDOs.add(statisticsDO);
      }
    }
    return statisticsDOs;
  }
}
