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

package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import com.antgroup.openspg.reasoner.lube.logical.EdgeVar;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.PathVar;
import com.antgroup.openspg.reasoner.lube.logical.RepeatPathVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import scala.Tuple2;
import scala.collection.JavaConversions;

public class KgGraphDropFieldsImpl implements Serializable {

  private static final long serialVersionUID = 3662110639574654863L;
  private final Map<String, Map<String, Object>> dropVertexAlias2PropertyMap = new HashMap<>();
  private final Map<String, Map<String, Object>> dropEdgeAlias2PropertyMap = new HashMap<>();

  private final Map<String, Map<String, Map<String, Object>>> dropPathEdgeAlias2PropertyMap =
      new HashMap<>();

  /** init */
  public KgGraphDropFieldsImpl(Set<Var> dropFieldSet) {
    for (Var field : dropFieldSet) {
      if (field instanceof NodeVar) {
        Tuple2<String, Set<String>> tuple2 = getDropInfo(field);
        dropVertex(tuple2._1(), tuple2._2());
      } else if (field instanceof EdgeVar) {
        Tuple2<String, Set<String>> tuple2 = getDropInfo(field);
        dropEdge(tuple2._1(), tuple2._2());
      } else if (field instanceof RepeatPathVar) {
        RepeatPathVar arrayVar = (RepeatPathVar) field;
        PathVar pathVar = arrayVar.pathVar();
        for (int i = 0; i < pathVar.elements().size(); ++i) {
          Var subVar = pathVar.elements().apply(i);
          Tuple2<String, Set<String>> tuple2 = getDropInfo(subVar);
          Map<String, Map<String, Object>> subMap =
              dropPathEdgeAlias2PropertyMap.computeIfAbsent(pathVar.name(), k -> new HashMap<>());
          Map<String, Object> propertyMap =
              subMap.computeIfAbsent(tuple2._1(), k -> new HashMap<>());
          for (String key : tuple2._2()) {
            propertyMap.put(key, null);
          }
        }
      }
    }
    clearEmptyDropInfo();
  }

  private void clearEmptyDropInfo() {
    this.dropVertexAlias2PropertyMap
        .entrySet()
        .removeIf(entry -> MapUtils.isEmpty(entry.getValue()));
    this.dropEdgeAlias2PropertyMap.entrySet().removeIf(entry -> MapUtils.isEmpty(entry.getValue()));
    this.dropPathEdgeAlias2PropertyMap
        .entrySet()
        .removeIf(
            entry -> {
              entry.getValue().entrySet().removeIf(entry2 -> MapUtils.isEmpty(entry2.getValue()));
              return MapUtils.isEmpty(entry.getValue());
            });
  }

  /** check need to drop op */
  public boolean needDrop() {
    if (!dropVertexAlias2PropertyMap.isEmpty()) {
      return true;
    }
    if (!dropEdgeAlias2PropertyMap.isEmpty()) {
      return true;
    }
    if (!dropPathEdgeAlias2PropertyMap.isEmpty()) {
      return true;
    }
    return false;
  }

  private Tuple2<String, Set<String>> getDropInfo(Var var) {
    String alias = null;
    Set<String> propertyNameSet = null;
    if (var instanceof NodeVar) {
      NodeVar nodeField = (NodeVar) var;
      alias = nodeField.name();
      propertyNameSet =
          JavaConversions.setAsJavaSet(nodeField.fields()).stream()
              .map(Field::name)
              .collect(Collectors.toSet());
    } else if (var instanceof EdgeVar) {
      EdgeVar edgeField = (EdgeVar) var;
      alias = edgeField.name();
      propertyNameSet =
          JavaConversions.setAsJavaSet(edgeField.fields()).stream()
              .map(Field::name)
              .collect(Collectors.toSet());
    }
    return new Tuple2<>(alias, propertyNameSet);
  }

  private void dropVertex(String alias, Set<String> propertySet) {
    Map<String, Object> propertyMap =
        this.dropVertexAlias2PropertyMap.computeIfAbsent(alias, k -> new HashMap<>());
    for (String key : propertySet) {
      propertyMap.put(key, null);
    }
  }

  private void dropEdge(String alias, Set<String> propertySet) {
    Map<String, Object> propertyMap =
        this.dropEdgeAlias2PropertyMap.computeIfAbsent(alias, k -> new HashMap<>());
    for (String key : propertySet) {
      propertyMap.put(key, null);
    }
  }

  /**
   * drop fields
   *
   * @param kgGraph
   */
  public void doDropFields(KgGraph<IVertexId> kgGraph) {
    for (Map.Entry<String, Map<String, Object>> entry :
        this.dropVertexAlias2PropertyMap.entrySet()) {
      kgGraph.setVertexProperty(entry.getKey(), entry.getValue(), 0L);
    }
    for (Map.Entry<String, Map<String, Object>> entry : this.dropEdgeAlias2PropertyMap.entrySet()) {
      kgGraph.setEdgeProperty(entry.getKey(), entry.getValue());
    }
  }
}
