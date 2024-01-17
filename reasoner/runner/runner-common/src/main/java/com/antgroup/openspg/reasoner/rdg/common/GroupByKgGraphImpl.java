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

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.NoneVertex;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.collections4.CollectionUtils;
import scala.Tuple2;

public class GroupByKgGraphImpl implements Serializable {
  private static final long serialVersionUID = 5982445397315165348L;

  protected final List<String> byAliasList;
  protected final List<Tuple2<String, String>> byPropertyList;

  protected final Set<String> byAliasSet;
  protected final Map<ByPropertyKey, Set<String>> byPropertyInfoMap = new TreeMap<>();

  protected final Pattern schema;
  protected final Long limit;

  private final KgGraphSplitStaticParameters staticParameters;

  private final KgGraphListProcess kgGraphListProcess;

  /** impl */
  public GroupByKgGraphImpl(
      List<String> byAliasList,
      List<Tuple2<String, String>> byPropertyList,
      KgGraphListProcess kgGraphListProcess,
      Pattern schema,
      Long limit) {
    this.byAliasList = byAliasList;
    this.byAliasSet = Sets.newHashSet(byAliasList);
    // add property alias to by alias
    byPropertyList.forEach(
        tuple2 -> {
          String tmpAlias = tuple2._1();
          if (Boolean.TRUE.equals(RunnerUtil.isVertexAlias(tmpAlias, schema))) {
            Set<String> set =
                byPropertyInfoMap.computeIfAbsent(
                    new ByPropertyKey(tmpAlias, true), k -> new TreeSet<>());
            set.add(tuple2._2());
          } else {
            Set<String> set =
                byPropertyInfoMap.computeIfAbsent(
                    new ByPropertyKey(tmpAlias, false), k -> new TreeSet<>());
            set.add(tuple2._2());
          }
        });
    this.byPropertyList = byPropertyList;
    this.schema = schema;
    this.limit = limit;
    this.staticParameters = new KgGraphSplitStaticParameters(this.byAliasSet, this.schema);
    this.kgGraphListProcess = kgGraphListProcess;
  }

  /** return list */
  public List<KgGraph<IVertexId>> groupReduce(Collection<KgGraph<IVertexId>> sameRootKgGraphList) {
    if (CollectionUtils.isEmpty(this.byAliasList) && CollectionUtils.isEmpty(this.byPropertyList)) {
      return doReduce(sameRootKgGraphList);
    }
    return doSplitThenReduce(sameRootKgGraphList);
  }

  private List<KgGraph<IVertexId>> doReduce(Collection<KgGraph<IVertexId>> sameRootKgGraphList) {
    return new ArrayList<>(this.kgGraphListProcess.reduce(sameRootKgGraphList));
  }

  private List<KgGraph<IVertexId>> doSplitThenReduce(Collection<KgGraph<IVertexId>> values) {
    Map<ByKey, List<KgGraph<IVertexId>>> kgGraphMap = new HashMap<>();
    Iterator<KgGraph<IVertexId>> valuesIt = values.iterator();
    while (valuesIt.hasNext()) {
      KgGraph<IVertexId> value = valuesIt.next();
      valuesIt.remove();
      List<KgGraph<IVertexId>> kgGraphList =
          value.split(byAliasSet, schema, staticParameters, null, limit);
      for (KgGraph<IVertexId> kgGraph : kgGraphList) {
        ByKey byKey = getByKey(kgGraph);
        List<KgGraph<IVertexId>> existKgGraphList =
            kgGraphMap.computeIfAbsent(byKey, k -> new ArrayList<>());
        existKgGraphList.add(kgGraph);
      }
    }
    List<KgGraph<IVertexId>> result = new ArrayList<>(kgGraphMap.size());
    Iterator<Map.Entry<ByKey, List<KgGraph<IVertexId>>>> mapIt = kgGraphMap.entrySet().iterator();
    while (mapIt.hasNext()) {
      Map.Entry<ByKey, List<KgGraph<IVertexId>>> entry = mapIt.next();
      mapIt.remove();
      List<KgGraph<IVertexId>> kgGraphList = entry.getValue();
      if (CollectionUtils.isNotEmpty(this.byPropertyList)) {
        // group by property
        Map<ByKey, List<KgGraph<IVertexId>>> kgGraphPropertyMap = new HashMap<>();
        for (KgGraph<IVertexId> kgGraph : kgGraphList) {
          ByKey byKey = getByPropertyKey(kgGraph);
          List<KgGraph<IVertexId>> samePropertyList =
              kgGraphPropertyMap.computeIfAbsent(byKey, k -> new ArrayList<>());
          samePropertyList.add(kgGraph);
        }

        for (List<KgGraph<IVertexId>> kgGraphListSameProperty : kgGraphPropertyMap.values()) {
          result.addAll(this.kgGraphListProcess.reduce(kgGraphListSameProperty));
        }
      } else {
        result.addAll(this.kgGraphListProcess.reduce(kgGraphList));
      }
    }
    return result;
  }

  protected ByKey getByPropertyKey(KgGraph<IVertexId> kgGraph) {
    Object[] objects = new Object[this.byPropertyList.size()];
    int index = 0;
    for (Map.Entry<ByPropertyKey, Set<String>> entry : this.byPropertyInfoMap.entrySet()) {
      String alias = entry.getKey().getAlias();
      boolean isVertex = entry.getKey().isVertex();
      IProperty property;
      if (isVertex) {
        property = kgGraph.getVertex(alias).get(0).getValue();
      } else {
        property = kgGraph.getEdge(alias).get(0).getValue();
      }
      for (String propertyName : entry.getValue()) {
        if (null == property) {
          objects[index++] = null;
        } else {
          objects[index++] = property.get(propertyName);
        }
      }
    }
    return new ByKey(objects);
  }

  protected ByKey getByKey(KgGraph<IVertexId> kgGraph) {
    IVertexId[] vertexIds = new IVertexId[this.byAliasList.size()];
    for (int i = 0; i < this.byAliasList.size(); ++i) {
      String alias = this.byAliasList.get(i);
      IVertex<IVertexId, IProperty> vertex = kgGraph.getVertex(alias).get(0);
      if (vertex instanceof NoneVertex) {
        vertexIds[i] = null;
        continue;
      }
      vertexIds[i] = vertex.getId();
    }
    return new ByKey(vertexIds);
  }

  private static class ByPropertyKey implements Comparable<ByPropertyKey>, Serializable {
    private final String alias;
    private final boolean isVertex;

    public ByPropertyKey(String alias, boolean isVertex) {
      this.alias = alias;
      this.isVertex = isVertex;
    }

    public String getAlias() {
      return alias;
    }

    public boolean isVertex() {
      return isVertex;
    }

    @Override
    public int compareTo(ByPropertyKey o) {
      int r = Objects.compare(this.alias, o.alias, Comparator.naturalOrder());
      if (0 != r) {
        return r;
      }
      return Objects.compare(this.isVertex, o.isVertex, Comparator.naturalOrder());
    }
  }

  /** vertex id array */
  public static class ByKey implements Serializable {
    protected final Object[] vertexIds;

    /** init */
    public ByKey(IVertexId[] vertexIds) {
      this.vertexIds = vertexIds;
    }

    public ByKey(Object[] objects) {
      this.vertexIds = objects;
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(vertexIds);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ByKey)) {
        return false;
      }
      ByKey other = (ByKey) obj;
      return Arrays.equals(this.vertexIds, other.vertexIds);
    }
  }
}
