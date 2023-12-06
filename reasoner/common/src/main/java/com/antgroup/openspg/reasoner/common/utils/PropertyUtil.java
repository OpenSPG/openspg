/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
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

/**
 * @author kejian
 * @version PropertyUtil.java, v 0.1 2023年05月18日 5:37 PM kejian
 */
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
