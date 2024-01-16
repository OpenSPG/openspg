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

package com.antgroup.openspg.reasoner.common.utils;

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.propertybuild.IPropertyBuilder;
import com.antgroup.openspg.reasoner.common.graph.propertybuild.impl.BasePropertyBuilder;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.util.Map;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "userlogger")
public class PropertyUtil {
  public static Boolean useBinaryProperty = false;

  public static IPropertyBuilder propertyBuilder = new BasePropertyBuilder();

  /** reset property util */
  public static void reset() {
    propertyBuilder.reset();
    useBinaryProperty = false;
    propertyBuilder = new BasePropertyBuilder();
  }

  /**
   * build version vertex property, return normal VertexVersionProperty or VertexBinaryProperty
   * based on useBinaryProperty
   *
   * @param vertexId
   * @param property
   * @return
   */
  public static IVersionProperty buildVertexProperty(
      IVertexId vertexId, Map<String, TreeMap<Long, Object>> property) {
    return propertyBuilder.buildVertexProperty(vertexId, property);
  }

  public static IProperty buildEdgeProperty(String edgeType, Map<String, Object> property) {
    return propertyBuilder.buildEdgeProperty(edgeType, property);
  }

  /** get version property value from version-value map */
  public static Object getVersionValue(Long version, TreeMap<Long, Object> versionValue) {
    if (null == versionValue) {
      return null;
    }
    if (null == version) {
      version = Long.MAX_VALUE;
    }
    // Gets the entry corresponding to the specified key;
    // if no such entry exists, returns the entry for the greatest key less than the specified key;
    // if no such entry exists, returns null.
    Map.Entry<Long, Object> entry = versionValue.floorEntry(version);
    if (null == entry) {
      return null;
    }
    return entry.getValue();
  }
}
