/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.graph.propertybuild;

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author kejian
 * @version IPropertyBuilder.java, v 0.1 2023年07月20日 5:05 PM kejian
 */
public interface IPropertyBuilder {

  /**
   * build vertex multi-version property
   *
   * @param vertexId
   * @param property
   * @return
   */
  IVersionProperty buildVertexProperty(
      IVertexId vertexId, Map<String, TreeMap<Long, Object>> property);

  /**
   * build edge property
   *
   * @param edgeType
   * @param property
   * @return
   */
  IProperty buildEdgeProperty(String edgeType, Map<String, Object> property);

  /** reset property */
  void reset();
}
