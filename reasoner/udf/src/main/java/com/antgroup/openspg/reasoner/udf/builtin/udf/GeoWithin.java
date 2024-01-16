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

package com.antgroup.openspg.reasoner.udf.builtin.udf;

import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.antgroup.openspg.reasoner.udf.utils.GeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;

@Slf4j(topic = "userlogger")
public class GeoWithin {

  /**
   * Check whether geometry1 within geometry2
   *
   * @param str1
   * @param str2
   * @return
   */
  @UdfDefine(name = "geo_within")
  public Boolean geoWithin(String str1, String str2) {
    try {
      Geometry geometry1 = GeoUtils.fromWKT(str1);
      Geometry geometry2 = GeoUtils.fromWKT(str2);
      return GeoUtils.within(geometry1, geometry2);
    } catch (Exception e) {
      log.error(String.format("geo_within input error, arg1=%s, arg2=%s", str1, str2));
      return null;
    }
  }
}
