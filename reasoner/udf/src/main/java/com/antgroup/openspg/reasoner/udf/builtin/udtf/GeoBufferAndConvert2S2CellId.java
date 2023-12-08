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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.udf.builtin.udtf;

import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdtf;
import com.antgroup.openspg.reasoner.udf.model.LinkedUdtfResult;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.antgroup.openspg.reasoner.udf.utils.GeoUtils;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Geometry;

@Slf4j(topic = "userlogger")
@UdfDefine(name = "geo_buffer_and_convert_2_s2CellId")
public class GeoBufferAndConvert2S2CellId extends BaseUdtf {

  /**
   * udtf input row data type list
   *
   * @return
   */
  @Override
  public List<KgType> getInputRowTypes() {
    return Lists.newArrayList(KTString$.MODULE$, KTDouble$.MODULE$);
  }

  /**
   * udtf result type list
   *
   * @return
   */
  @Override
  public List<KgType> getResultTypes() {
    return Lists.newArrayList(KTObject$.MODULE$);
  }

  @Override
  public void process(List<Object> args) {
    if (null == args || args.size() != 2) {
      throw new RuntimeException("geo_buffer_and_convert_2_s2CellId should have 2 parameters");
    }
    if (null == args.get(0) || StringUtils.isBlank(args.get(0).toString())) {
      forward(Lists.newArrayList());
      return;
    }
    if (null == args.get(1) || StringUtils.isBlank(args.get(1).toString())) {
      throw new RuntimeException(
          "geo_buffer_and_convert_2_s2CellId 2nd parameter distance should not empty");
    }

    LinkedUdtfResult result = new LinkedUdtfResult();
    String wktString = args.get(0).toString();
    Double scale = Double.parseDouble(args.get(1).toString());
    try {
      Geometry geometry = GeoUtils.fromWKT(wktString);
      Geometry bufferedGeometry = geometry;
      if (scale > 0) {
        bufferedGeometry = GeoUtils.buffer(geometry, scale);
      }
      List<String> s2CellIdList = GeoUtils.getCoveredS2CellIdList(bufferedGeometry);
      result.getTargetVertexIdList().addAll(s2CellIdList);
      forward(Lists.newArrayList(result));
    } catch (Exception e) {
      log.error(
          String.format(
              "GeoBufferAndConvert2S2Token error, wkt=%s, scale=%s, errMsg = %s",
              wktString, scale, e.getMessage()));
      forward(Lists.newArrayList());
    }
  }
}
