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

package com.antgroup.openspg.reasoner.graphstate.impl;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.model.MergeTypeEnum;
import com.antgroup.openspg.reasoner.lube.common.rule.Rule;
import com.antgroup.openspg.reasoner.utils.RocksDBUtil;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteOptions;
import scala.Tuple2;

/**
 * GraphState based on Rocksdb vertex key is V_TYPEID_ID_PLACEHOLDER_WINDOWS, value is vertex
 * property edge key is E_EDGETYPE_DIRECTION_VERTEXID_WINDOW, value is edges
 */
@Slf4j(topic = "userlogger")
public class RocksdbGraphState implements GraphState<IVertexId> {
  public final transient RocksDB rocksDB;
  private final transient IRocksDBGraphStateHelper helper;

  public RocksdbGraphState(RocksDB rocksDB, IRocksDBGraphStateHelper helper) {
    this.rocksDB = rocksDB;
    this.helper = helper;
  }

  /**
   * init parameters
   *
   * @param param
   */
  @Override
  public void init(Map<String, String> param) {}

  /**
   * wrap vertex to rocksdb storage
   *
   * @param vertex
   * @return
   */
  private Tuple2<byte[], IProperty> wrapVertex(IVertex<IVertexId, IProperty> vertex, long window) {
    return new Tuple2<>(
        RocksDBUtil.rocksdbKeyAppendWindow(
            RocksDBUtil.buildRocksDBVertexKeyWithoutWindow(vertex.getId()), window),
        vertex.getValue());
  }

  /**
   * add vertex with all version properties to graph state
   *
   * @param vertex
   */
  @Override
  public void addVertex(IVertex<IVertexId, IProperty> vertex) {
    this.addVertex(vertex, 0L);
  }

  /**
   * add vertex with all version properties to graph state
   *
   * @param vertex
   */
  @Override
  public void addVertex(IVertex<IVertexId, IProperty> vertex, Long version) {
    if (vertex.getValue() instanceof IVersionProperty) {
      Tuple2<byte[], IProperty> wrapResult = wrapVertex(vertex, version);
      writeData(wrapResult._1, wrapResult._2);
      return;
    }
    log.error("Only IVersionProperty is supported.");
    throw new RuntimeException("Only IVersionProperty is supported.");
  }

  /**
   * merge properties of vertex just write, good performance
   *
   * @param id
   * @param property
   * @param mergeType
   * @param version
   */
  @Override
  public void mergeVertexProperty(
      IVertexId id, Map<String, Object> property, MergeTypeEnum mergeType, Long version) {
    Preconditions.checkNotNull(id, "id is null");
    Preconditions.checkNotNull(property, "property is null");
    Preconditions.checkNotNull(mergeType, "mergeType is null");

    if (MergeTypeEnum.APPEND.equals(mergeType)) {
      throw new RuntimeException("APPEND not supported");
    }
    IVersionProperty versionProperty = PropertyUtil.buildVertexProperty(id, null);
    for (String key : property.keySet()) {
      versionProperty.put(key, property.get(key), version);
    }
    if (versionProperty.getSize() == 0) {
      log.warn("no property need to be merged");
      return;
    }
    IVertex<IVertexId, IProperty> newVertex = new Vertex<>(id, versionProperty);
    Tuple2<byte[], IProperty> wrapResult =
        wrapVertex(newVertex, this.helper.getWriteWindow(version));
    writeData(wrapResult._1, wrapResult._2);
  }

  /**
   * set the properties of the vertex to be cached if not set, all properties are cached by default
   *
   * @param properties
   */
  @Override
  public void setVertexCacheProperty(Map<String, Set<String>> properties) {
    throw new NotImplementedException("setVertexCacheProperty is not implemented", null);
  }

  private static final WriteOptions DEFAULT_WRITE_OPTIONS = new WriteOptions();

  static {
    DEFAULT_WRITE_OPTIONS.setDisableWAL(true);
  }

  /**
   * write data to rocksdb
   *
   * @param key
   * @param value
   */
  private void writeData(byte[] key, Object value) {
    if (key == null || key.length == 0) {
      log.warn("writeData keys is empty");
      return;
    }
    try {
      byte[] values = helper.object2Byte(value);
      rocksDB.put(DEFAULT_WRITE_OPTIONS, key, values);
    } catch (RocksDBException e) {
      throw new RuntimeException("rocksdb put error, key=" + Arrays.toString(key), e);
    }
  }

  /**
   * @param keyPrefix rocksdb key prefix
   * @param windowRange windows
   * @return
   */
  private List<byte[]> readData(byte[] keyPrefix, Tuple2<Long, Long> windowRange) {
    byte[] seekKey;
    if (null == windowRange) {
      seekKey = RocksDBUtil.rocksdbKeyAppendWindow(keyPrefix, 0L);
    } else {
      seekKey = RocksDBUtil.rocksdbKeyAppendWindow(keyPrefix, windowRange._1());
    }
    List<byte[]> result = new ArrayList<>();

    // rocksdb seek
    ReadOptions readOptions = new ReadOptions();
    readOptions.setPrefixSameAsStart(true);
    readOptions.setTotalOrderSeek(false);
    RocksIterator it = rocksDB.newIterator(readOptions);
    it.seek(seekKey);

    while (it.isValid()) {
      byte[] rocksdbKey = it.key();
      boolean prefixEquals = Utils.compareByteArrayPrefix(keyPrefix, rocksdbKey);
      if (!prefixEquals) {
        break;
      }

      if (null != windowRange) {
        long window = RocksDBUtil.getRocksdbKeyWindow(rocksdbKey);
        if (!RunnerUtil.between(windowRange._1(), windowRange._2(), window)) {
          it.next();
          continue;
        }
      }
      result.add(it.value());
      it.next();
    }
    it.close();
    return result;
  }

  /**
   * collapse multiple rocksdb vertex properties to one versionVertexProperty
   *
   * @param propertyByteList
   * @return
   */
  private Map<String, TreeMap<Long, Object>> collapseVertexProperty(List<byte[]> propertyByteList) {
    List<IVersionProperty> vertexVersionProperties =
        propertyByteList.stream()
            .map(
                bytes -> {
                  Object obj = helper.byte2Object(bytes);
                  if (!(obj instanceof IVersionProperty)) {
                    throw new RuntimeException("vertex property is not IVersionProperty");
                  }
                  return (IVersionProperty) obj;
                })
            .collect(Collectors.toList());

    // merge VertexVersionProperty
    Map<String, TreeMap<Long, Object>> resultMap = new HashMap<>();
    for (IVersionProperty property : vertexVersionProperties) {
      for (String key : property.getKeySet()) {
        TreeMap<Long, Object> thisVersionValueMap =
            resultMap.computeIfAbsent(key, k -> new TreeMap<>());
        Object otherVersionValueMap = property.getVersionValue(key);
        if (null == otherVersionValueMap) {
          continue;
        }
        thisVersionValueMap.putAll(((Map<Long, Object>) otherVersionValueMap));
      }
    }
    return resultMap;
  }

  /**
   * get vertex with the specific version properties
   *
   * @param id
   * @param version if null return vertex with the all version properties, if Long.MAX_VALUE return
   *     newest properties
   * @return
   */
  @Override
  public IVertex<IVertexId, IProperty> getVertex(IVertexId id, Long version) {
    byte[] rocksdbKeyPrefix = RocksDBUtil.buildRocksDBVertexKeyWithoutWindow(id);
    Tuple2<Long, Long> windowRange = this.helper.mapVersion2WindowRange(version);
    List<byte[]> propertyList = readData(rocksdbKeyPrefix, windowRange);
    if (CollectionUtils.isEmpty(propertyList)) {
      return null;
    }
    Map<String, TreeMap<Long, Object>> tmpProps = collapseVertexProperty(propertyList);
    if (null == version) {
      return new Vertex<>(id, PropertyUtil.buildVertexProperty(id, tmpProps));
    }

    Map<String, TreeMap<Long, Object>> resultProps = new HashMap<>();
    // filter specific version property
    for (String key : tmpProps.keySet()) {
      TreeMap<Long, Object> versionValue = tmpProps.get(key);
      Object value = PropertyUtil.getVersionValue(version, versionValue);
      TreeMap<Long, Object> tmpMap = new TreeMap<>();
      tmpMap.put(version, value);
      resultProps.put(key, tmpMap);
    }
    return new Vertex<>(id, PropertyUtil.buildVertexProperty(id, resultProps));
  }

  @Override
  public IVertex<IVertexId, IProperty> getVertex(IVertexId id, Long version, Rule rule) {
    return getVertex(id, version);
  }

  /**
   * wrap edge to rocksdb storage
   *
   * @param edges
   * @param direction
   * @param vertexId
   * @return
   */
  private List<Tuple2<byte[], IProperty>> wrapEdge(
      List<IEdge<IVertexId, IProperty>> edges, Direction direction, IVertexId vertexId) {
    List<Tuple2<byte[], IProperty>> wrapEdgeList = new ArrayList<>();
    Map<String, Map<Long, List<IEdge<IVertexId, IProperty>>>> type2Version2EdgeMap =
        new HashMap<>();
    for (IEdge<IVertexId, IProperty> edge : edges) {
      String p = edge.getType();
      Map<Long, List<IEdge<IVertexId, IProperty>>> version2EdgeListMap =
          type2Version2EdgeMap.computeIfAbsent(p, k -> new HashMap<>());
      List<IEdge<IVertexId, IProperty>> edgeList =
          version2EdgeListMap.computeIfAbsent(
              this.helper.getWriteWindow(edge.getVersion()), k -> new ArrayList<>());
      edgeList.add(edge);
    }
    for (String type : type2Version2EdgeMap.keySet()) {
      Map<Long, List<IEdge<IVertexId, IProperty>>> version2EdgeListMap =
          type2Version2EdgeMap.get(type);
      byte[] edgeKeyPrefix =
          RocksDBUtil.buildRocksDBEdgeKeyWithoutWindow(type, direction, vertexId);
      for (long window : version2EdgeListMap.keySet()) {
        byte[] edgeKey = RocksDBUtil.rocksdbKeyAppendWindow(edgeKeyPrefix, window);
        IProperty edgeProperty = new EdgeProperty();
        edgeProperty.put("edges", version2EdgeListMap.get(window));
        wrapEdgeList.add(new Tuple2<>(edgeKey, edgeProperty));
      }
    }
    return wrapEdgeList;
  }

  /**
   * add inEdges and outEdges of a vertex
   *
   * @param vertexId
   * @param inEdges
   * @param outEdges
   */
  @Override
  public void addEdges(
      IVertexId vertexId,
      List<IEdge<IVertexId, IProperty>> inEdges,
      List<IEdge<IVertexId, IProperty>> outEdges) {
    if (CollectionUtils.isNotEmpty(outEdges)) {
      wrapEdge(outEdges, Direction.OUT, vertexId)
          .forEach(tuple -> writeData(tuple._1(), tuple._2()));
    }

    if (CollectionUtils.isNotEmpty(inEdges)) {
      wrapEdge(inEdges, Direction.IN, vertexId).forEach(tuple -> writeData(tuple._1(), tuple._2()));
    }
  }

  /**
   * update specific edge properties edges are redundantly stored and only one edge has been updated
   * need to call (o, p, s, t) to update the other edge
   *
   * @param s
   * @param p
   * @param o
   * @param version
   * @param property
   */
  @Override
  public void updateEdgeProperty(
      IVertexId s, String p, IVertexId o, Long version, IProperty property) {
    throw new NotImplementedException("updateEdgeProperty not implement", null);
  }

  /**
   * merge specific edge properties edges are redundantly stored and only one edge has been updated
   * need to call (o, p, s, t) to update the other edge
   *
   * @param s
   * @param p
   * @param o
   * @param version
   * @param property
   * @param mergeType
   */
  @Override
  public void mergeEdgeProperty(
      IVertexId s,
      String p,
      IVertexId o,
      Long version,
      Direction direction,
      Map<String, Object> property,
      MergeTypeEnum mergeType) {
    Preconditions.checkNotNull(s, "mergeEdgeProperty s is null");
    Preconditions.checkNotNull(p, "mergeEdgeProperty p is null");
    Preconditions.checkNotNull(o, "mergeEdgeProperty o is null");
    Preconditions.checkNotNull(mergeType, "mergeType is null");
    Preconditions.checkNotNull(version, "mergeEdgeProperty version is null");
    Preconditions.checkNotNull(direction, "direction version is null");
    Preconditions.checkNotNull(property, "mergeEdgeProperty property is null");
    if (MergeTypeEnum.APPEND.equals(mergeType)) {
      throw new RuntimeException("APPEND not supported");
    }
    if (property.isEmpty()) {
      log.warn("no property need to be merged");
      return;
    }

    IProperty edgeProperty = PropertyUtil.buildEdgeProperty(p, property);
    IEdge<IVertexId, IProperty> edge = new Edge<>(s, o, edgeProperty, version, direction, p);
    List<IEdge<IVertexId, IProperty>> inEdges = new ArrayList<>();
    List<IEdge<IVertexId, IProperty>> outEdges = new ArrayList<>();
    if (Direction.IN.equals(direction) || Direction.BOTH.equals(direction)) {
      inEdges.add(edge);
    }
    if (Direction.OUT.equals(direction) || Direction.BOTH.equals(direction)) {
      outEdges.add(edge);
    }
    addEdges(s, inEdges, outEdges);
  }

  /**
   * set the properties of the edge to be cached if not set, all properties are cached by default
   *
   * @param properties
   */
  @Override
  public void setEdgeCacheProperty(Map<String, Set<String>> properties) {
    throw new NotImplementedException("setEdgeCacheProperty is not implemented", null);
  }

  /**
   * get the edge without property of the specific type of vertex
   *
   * @param vertexId
   * @param startVersion if null, get default version edge of the vertex
   * @param endVersion if null, get default version edge of the vertex
   * @param types if null, get all type edges of the vertex
   * @param direction
   * @return key is edge type, value is direct_o_version
   */
  @Override
  public List<IEdge<IVertexId, IProperty>> getEdgesWithoutProperty(
      IVertexId vertexId,
      Long startVersion,
      Long endVersion,
      Set<String> types,
      Direction direction) {
    throw new NotImplementedException("getEdgesWithoutProperty is not implemented", null);
  }

  /**
   * get specific types edges of vertex
   *
   * @param vertexId
   * @param startVersion if null, get default version edge of the vertex
   * @param endVersion if null, get default version edge of the vertex
   * @param types not empty
   * @param direction
   * @return
   */
  @Override
  public List<IEdge<IVertexId, IProperty>> getEdges(
      IVertexId vertexId,
      Long startVersion,
      Long endVersion,
      Set<String> types,
      Direction direction) {
    if (CollectionUtils.isEmpty(types)) {
      throw new RuntimeException("getEdges input types is empty");
    }
    if (null == direction) {
      throw new RuntimeException("getEdges input direction is null");
    }

    List<IEdge<IVertexId, IProperty>> result = new ArrayList<>();

    if (Direction.OUT.equals(direction) || Direction.BOTH.equals(direction)) {
      result.addAll(collectEdges(vertexId, startVersion, endVersion, types, Direction.OUT));
    }
    if (Direction.IN.equals(direction) || Direction.BOTH.equals(direction)) {
      result.addAll(collectEdges(vertexId, startVersion, endVersion, types, Direction.IN));
    }
    return result;
  }

  @Override
  public List<IEdge<IVertexId, IProperty>> getEdges(
      IVertexId vertexId,
      Long startVersion,
      Long endVersion,
      Set<String> types,
      Direction direction,
      Map<String, List<Rule>> typeAndRuleMap) {
    return getEdges(vertexId, startVersion, endVersion, types, direction);
  }

  /**
   * collect multiple rocksdb edges of one edge
   *
   * @param vertexId
   * @param startVersion
   * @param endVersion
   * @param types
   * @param direction
   * @return
   */
  private List<IEdge<IVertexId, IProperty>> collectEdges(
      IVertexId vertexId,
      Long startVersion,
      Long endVersion,
      Set<String> types,
      Direction direction) {
    List<IEdge<IVertexId, IProperty>> result = new ArrayList<>();
    for (String type : types) {
      byte[] rocksdbKeyPrefix =
          RocksDBUtil.buildRocksDBEdgeKeyWithoutWindow(type, direction, vertexId);
      List<byte[]> bytesList =
          readData(rocksdbKeyPrefix, this.helper.mapVersion2WindowRange(startVersion, endVersion));
      if (CollectionUtils.isEmpty(bytesList)) {
        continue;
      }
      List<Map<String, IEdge<IVertexId, IProperty>>> edgeList =
          bytesList.stream()
              .map(
                  bytes -> {
                    Object obj = helper.byte2Object(bytes);
                    if (!(obj instanceof EdgeProperty)) {
                      throw new RuntimeException(
                          "The data that getEdges reads from rocksdb is not EdgeProperty type");
                    }
                    if (!((EdgeProperty) obj).isKeyExist("edges")) {
                      throw new RuntimeException("getEdges EdgeProperty not contain edges");
                    }
                    List<IEdge<IVertexId, IProperty>> edges =
                        ((List<IEdge<IVertexId, IProperty>>) ((EdgeProperty) obj).get("edges"));
                    Map<String, IEdge<IVertexId, IProperty>> edgeMap = new HashMap<>();
                    for (IEdge<IVertexId, IProperty> edge : edges) {
                      // spot identifies the unique edge
                      String key = RunnerUtil.getEdgeIdentifier(edge);
                      IEdge<IVertexId, IProperty> oldEdge = edgeMap.get(key);
                      if (null == oldEdge) {
                        edgeMap.put(key, edge);
                        continue;
                      }
                      // Merge the Properties of edges in the same window
                      for (String propertyName : edge.getValue().getKeySet()) {
                        oldEdge.getValue().put(propertyName, edge.getValue().get(propertyName));
                      }
                    }
                    return edgeMap;
                  })
              .collect(Collectors.toList());

      // Merge the Properties of edges in the different window
      Map<String, IEdge<IVertexId, IProperty>> resultEdgeMap = new HashMap<>();
      for (Map<String, IEdge<IVertexId, IProperty>> map : edgeList) {
        if (resultEdgeMap.isEmpty()) {
          resultEdgeMap.putAll(map);
          continue;
        }
        mergeEdgeMap(resultEdgeMap, map);
      }
      result.addAll(resultEdgeMap.values());
    }

    if (null == startVersion || null == endVersion) {
      return result;
    }
    return result.stream()
        .filter(e -> RunnerUtil.between(startVersion, endVersion, e.getVersion()))
        .collect(Collectors.toList());
  }

  /**
   * merge the multiple edge properties of one edge
   *
   * @param oldEdgeMap
   * @param newEdgeMap
   */
  private void mergeEdgeMap(
      Map<String, IEdge<IVertexId, IProperty>> oldEdgeMap,
      Map<String, IEdge<IVertexId, IProperty>> newEdgeMap) {
    for (String spot : newEdgeMap.keySet()) {
      if (!oldEdgeMap.containsKey(spot)) {
        oldEdgeMap.put(spot, newEdgeMap.get(spot));
        continue;
      }
      IEdge<IVertexId, IProperty> oldEdge = oldEdgeMap.get(spot);
      IEdge<IVertexId, IProperty> newEdge = newEdgeMap.get(spot);
      for (String propertyName : newEdge.getValue().getKeySet()) {
        oldEdge.getValue().put(propertyName, newEdge.getValue().get(propertyName));
      }
    }
  }

  /**
   * get specific type vertex iterator
   *
   * @param vertexType
   * @return
   */
  @Override
  public Iterator<IVertex<IVertexId, IProperty>> getVertexIterator(Set<String> vertexType) {
    return getVertexIterator(
        vertex -> {
          String type = (vertex.getId()).getType();
          if (null == vertexType) {
            return true;
          }
          return vertexType.contains(type);
        });
  }

  private RocksIterator getRocksIterator() {
    ReadOptions readOptions = new ReadOptions();
    readOptions.setPrefixSameAsStart(false);
    readOptions.setTotalOrderSeek(true);
    return rocksDB.newIterator(readOptions);
  }

  /**
   * get vertex iterator with user-defined filter
   *
   * @param filter
   * @return
   */
  @Override
  public Iterator<IVertex<IVertexId, IProperty>> getVertexIterator(
      Predicate<IVertex<IVertexId, IProperty>> filter) {
    RocksIterator it = getRocksIterator();
    it.seek(getVertexPrefix());
    if (!it.isValid()) {
      log.warn("Rocksdb has not contain vertex prefix key");
      return new Iterator<IVertex<IVertexId, IProperty>>() {
        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public IVertex<IVertexId, IProperty> next() {
          return null;
        }
      };
    }

    return new Iterator<IVertex<IVertexId, IProperty>>() {
      private IVertex<IVertexId, IProperty> cacheVertex = null;
      private byte[] curVertexKey = it.key();

      @Override
      public boolean hasNext() {
        if (null == cacheVertex) {
          while (true) {
            IVertex<IVertexId, IProperty> resultVertex = getNext();
            if (null == resultVertex) {
              break;
            } else if (filter.test(resultVertex)) {
              cacheVertex = resultVertex;
              break;
            }
          }
        }
        return null != cacheVertex;
      }

      @Override
      public IVertex<IVertexId, IProperty> next() {
        IVertex<IVertexId, IProperty> result = cacheVertex;
        cacheVertex = null;
        return result;
      }

      private IVertex<IVertexId, IProperty> getNext() {
        List<byte[]> curVertexPropertyList = new ArrayList<>();
        byte[] nextVertexKey = null;
        while (it.isValid()) {
          byte[] vertexKey = it.key();
          if (!RocksDBUtil.checkSameVertexKey(curVertexKey, vertexKey)) {
            nextVertexKey = vertexKey;
            break;
          }
          curVertexPropertyList.add(it.value());
          it.next();
        }

        if (curVertexPropertyList.isEmpty()) {
          return null;
        }
        IVertexId vertexId = RocksDBUtil.extractVertexId(curVertexKey);
        IProperty vertexProperty =
            PropertyUtil.buildVertexProperty(
                vertexId, collapseVertexProperty(curVertexPropertyList));
        IVertex<IVertexId, IProperty> resultVertex = new Vertex<>(vertexId, vertexProperty);
        curVertexKey = nextVertexKey;
        return resultVertex;
      }
    };
  }

  /**
   * get specific type edge iterator
   *
   * @param edgeType
   * @return
   */
  @Override
  public Iterator<IEdge<IVertexId, IProperty>> getEdgeIterator(Set<String> edgeType) {
    throw new NotImplementedException("getEdgeIterator is not implemented", null);
  }

  /**
   * get edge iterator with user-defined filter
   *
   * @param filter
   * @return
   */
  @Override
  public Iterator<IEdge<IVertexId, IProperty>> getEdgeIterator(
      Predicate<IEdge<IVertexId, IProperty>> filter) {
    throw new NotImplementedException("getEdgeIterator is not implemented", null);
  }

  /**
   * get the byte array of vertex prefix
   *
   * @return
   */
  private byte[] getVertexPrefix() {
    return RocksDBUtil.VERTEX_FLAG.getBytes();
  }

  /** checkPoint */
  @Override
  public void checkPoint() {}

  @Override
  public void close() {
    this.rocksDB.close();
  }
}
