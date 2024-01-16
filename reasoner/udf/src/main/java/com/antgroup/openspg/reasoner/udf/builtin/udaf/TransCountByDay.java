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

package com.antgroup.openspg.reasoner.udf.builtin.udaf;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.antgroup.openspg.reasoner.udf.utils.DateUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

@Slf4j(topic = "userlogger")
public class TransCountByDay {
  @UdfDefine(name = "trans_count_by_day")
  public static class TransCountByDayOp implements BaseUdaf {
    private int resultCount = 0;
    private String timeName = null;
    private String timeUnit = "s";
    private Integer preDayThreshold = null;
    private String op = null;
    private List<IEdge<IVertexId, IProperty>> propertyList = new ArrayList<>();

    @Override
    public KgType getInputRowType() {
      return KTObject$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTLong$.MODULE$;
    }

    @Override
    public void initialize(Object... params) {
      timeName = (String) params[0];
      timeUnit = (String) params[1];
      preDayThreshold = (Integer) params[2];
      op = (String) params[3];
    }

    @Override
    public void update(Object row) {
      if (row instanceof IEdge) {
        propertyList.add((IEdge<IVertexId, IProperty>) row);
      }
    }

    @Override
    public void merge(BaseUdaf function) {}

    @Override
    public Object evaluate() {
      Map<String, List<IEdge<IVertexId, IProperty>>> groupTimeMap = new HashMap<>();
      for (IEdge<IVertexId, IProperty> edge : propertyList) {
        String aggKey =
            second2Day(toSec(Long.parseLong(edge.getValue().get(timeName).toString()), timeUnit));
        groupTimeMap.computeIfAbsent(aggKey, k -> new ArrayList<>()).add(edge);
      }
      for (List<IEdge<IVertexId, IProperty>> edges : groupTimeMap.values()) {
        if (judgeThreshold(edges.size())) {
          resultCount++;
        }
      }
      return resultCount;
    }

    private boolean judgeThreshold(int size) {
      if ("large".equals(op)) {
        return size > preDayThreshold;
      } else if ("small".equals(op)) {
        return size < preDayThreshold;
      }
      throw new NotImplementedException(op + " not impl");
    }

    private long toSec(long timestamp, String unit) {
      if ("ms".equals(unit)) {
        return timestamp / 1000;
      }
      if ("s".equals(unit)) {
        return timestamp;
      }
      if ("us".equals(unit)) {
        return timestamp / 1000 / 1000;
      }
      throw new RuntimeException("time unit need in s/ms/us, but this is " + unit);
    }

    private String second2Day(long startTime) {
      return DateUtils.second2Str(startTime, DateUtils.SIMPLE_DATE_FORMAT);
    }
  }
}
