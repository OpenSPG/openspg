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
package com.antgroup.openspg.reasoner.warehouse.common.config;

import com.antgroup.openspg.reasoner.common.utils.SetsUtils;
import com.antgroup.openspg.reasoner.lube.catalog.AbstractConnection;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class GraphLoaderConfig implements Serializable {
  /** edge truncate threshold for each edge type */
  public static final int DEFAULT_EDGE_TRUNCATE_THRESHOLD = 8 * 10000;

  /** edge truncate threshold for all edge */
  public static final int DEFAULT_EDGE_TRUNCATE_THRESHOLD_GLOBALLY = 10 * 10000;

  /** total worker count in cluster */
  protected int totalWorkerCount;

  /** current worker index in cluster, count from 0 */
  protected int curWorkerIndex;

  /** maximum edge threshold globally */
  protected int edgeTruncateThreshold = DEFAULT_EDGE_TRUNCATE_THRESHOLD_GLOBALLY;

  /** vertex loader config */
  protected Set<VertexLoaderConfig> vertexLoaderConfigs;

  /** edge loader config */
  protected Set<EdgeLoaderConfig> edgeLoaderConfigs;

  private StartVertexConfig startVertexConfig;

  /** graph version config */
  private GraphVersionConfig graphVersionConfig;

  public StartVertexConfig getStartVertexConfig() {
    return startVertexConfig;
  }

  public void setStartVertexConfig(StartVertexConfig startVertexConfig) {
    this.startVertexConfig = startVertexConfig;
  }

  /** kgstate schema url */
  protected String schemaUrl = null;

  /** enable binary property or not */
  protected Boolean binary = false;

  private String namespaceUrl = null;

  /** kg reasoner task id */
  private String taskId = "";

  public int getTotalWorkerCount() {
    return totalWorkerCount;
  }

  public void setTotalWorkerCount(int totalWorkerCount) {
    this.totalWorkerCount = totalWorkerCount;
  }

  public int getCurWorkerIndex() {
    return curWorkerIndex;
  }

  public void setCurWorkerIndex(int curWorkerIndex) {
    this.curWorkerIndex = curWorkerIndex;
  }

  public int getEdgeTruncateThreshold() {
    return edgeTruncateThreshold;
  }

  public void setEdgeTruncateThreshold(int edgeTruncateThreshold) {
    this.edgeTruncateThreshold = edgeTruncateThreshold;
  }

  public void setEdgeTruncateEachTypeThreshold(int edgeTruncateThreshold) {
    for (EdgeLoaderConfig edgeLoaderConfig : this.edgeLoaderConfigs) {
      edgeLoaderConfig.setEdgeTruncateThreshold(edgeTruncateThreshold);
    }
  }

  public Set<VertexLoaderConfig> getVertexLoaderConfigs() {
    return vertexLoaderConfigs;
  }

  public void setVertexLoaderConfigs(Set<VertexLoaderConfig> vertexLoaderConfigs) {
    this.vertexLoaderConfigs = vertexLoaderConfigs;
  }

  public Set<EdgeLoaderConfig> getEdgeLoaderConfigs() {
    return edgeLoaderConfigs;
  }

  public void setEdgeLoaderConfigs(Set<EdgeLoaderConfig> edgeLoaderConfigs) {
    this.edgeLoaderConfigs = edgeLoaderConfigs;
  }

  public String getNamespaceUrl() {
    return namespaceUrl;
  }

  public void setNamespaceUrl(String namespaceUrl) {
    this.namespaceUrl = namespaceUrl;
  }

  public Set<String> allVertexTypes() {
    return SetsUtils.map(vertexLoaderConfigs, VertexLoaderConfig::getVertexType);
  }

  public Set<String> allEdgeTypes() {
    return SetsUtils.map(edgeLoaderConfigs, EdgeLoaderConfig::getEdgeType);
  }

  public VertexLoaderConfig getVertexLoadConfig(String vertexType) {
    if (CollectionUtils.isEmpty(vertexLoaderConfigs)) {
      return null;
    }
    return vertexLoaderConfigs.stream()
        .filter(x -> vertexType.equals(x.getVertexType()))
        .findAny()
        .orElse(null);
  }

  public EdgeLoaderConfig getEdgeLoadConfig(String edgeType) {
    if (CollectionUtils.isEmpty(edgeLoaderConfigs)) {
      return null;
    }
    return edgeLoaderConfigs.stream()
        .filter(x -> edgeType.equals(x.getEdgeType()))
        .findAny()
        .orElse(null);
  }

  public GraphLoaderConfig merge(GraphLoaderConfig other) {
    if (vertexLoaderConfigs == null) {
      this.vertexLoaderConfigs = other.vertexLoaderConfigs;
    } else {
      Map<String, VertexLoaderConfig> vertexLoaderConfigMap = new HashMap<>();
      for (VertexLoaderConfig vertexLoaderConfig : vertexLoaderConfigs) {
        vertexLoaderConfigMap.put(vertexLoaderConfig.getVertexType(), vertexLoaderConfig);
      }
      for (VertexLoaderConfig vertexLoaderConfig : other.vertexLoaderConfigs) {
        String vType = vertexLoaderConfig.getVertexType();
        if (vertexLoaderConfigMap.containsKey(vType)) {
          vertexLoaderConfigMap.put(
              vType, vertexLoaderConfigMap.get(vType).merge(vertexLoaderConfig));
        } else {
          vertexLoaderConfigMap.put(vType, vertexLoaderConfig);
        }
      }
      this.vertexLoaderConfigs = new HashSet<>(vertexLoaderConfigMap.values());
    }
    if (edgeLoaderConfigs == null) {
      this.edgeLoaderConfigs = other.edgeLoaderConfigs;
    } else {
      Map<String, EdgeLoaderConfig> edgeLoaderConfigMap = new HashMap<>();
      for (EdgeLoaderConfig edgeLoaderConfig : edgeLoaderConfigs) {
        edgeLoaderConfigMap.put(edgeLoaderConfig.getEdgeType(), edgeLoaderConfig);
      }
      for (EdgeLoaderConfig edgeLoaderConfig : other.edgeLoaderConfigs) {
        String eType = edgeLoaderConfig.getEdgeType();
        if (edgeLoaderConfigMap.containsKey(eType)) {
          edgeLoaderConfigMap.put(eType, edgeLoaderConfigMap.get(eType).merge(edgeLoaderConfig));
        } else {
          edgeLoaderConfigMap.put(eType, edgeLoaderConfig);
        }
      }
      this.edgeLoaderConfigs = new HashSet<>(edgeLoaderConfigMap.values());
    }
    return this;
  }

  @Override
  public int hashCode() {
    List<String> vertexTypeList =
        vertexLoaderConfigs.stream()
            .map(VertexLoaderConfig::getVertexType)
            .sorted()
            .collect(Collectors.toList());
    List<String> edgeTypeList =
        edgeLoaderConfigs.stream()
            .map(EdgeLoaderConfig::getEdgeType)
            .sorted()
            .collect(Collectors.toList());
    return Objects.hash(vertexTypeList, edgeTypeList);
  }

  /** convert to java object */
  public void toJavaConfig() {
    this.vertexLoaderConfigs.forEach(
        new Consumer<VertexLoaderConfig>() {
          @Override
          public void accept(VertexLoaderConfig vertexLoaderConfig) {
            vertexLoaderConfig.setNeedProperties(
                Sets.newHashSet(vertexLoaderConfig.getNeedProperties()));
            vertexLoaderConfig.setPropertiesFilterRules(
                Lists.newArrayList(vertexLoaderConfig.getPropertiesFilterRules()));
          }
        });
    this.vertexLoaderConfigs = Sets.newHashSet(this.vertexLoaderConfigs);

    this.edgeLoaderConfigs.forEach(
        new Consumer<EdgeLoaderConfig>() {
          @Override
          public void accept(EdgeLoaderConfig edgeLoaderConfig) {
            edgeLoaderConfig.setNeedProperties(
                Sets.newHashSet(edgeLoaderConfig.getNeedProperties()));
            edgeLoaderConfig.setPropertiesFilterRules(
                Lists.newArrayList(edgeLoaderConfig.getPropertiesFilterRules()));
          }
        });
    this.edgeLoaderConfigs = Sets.newHashSet(this.edgeLoaderConfigs);
  }

  /** set connectionUri */
  public void setConnectionUri(AbstractConnection connection) {
    if (CollectionUtils.isNotEmpty(this.vertexLoaderConfigs)) {
      this.vertexLoaderConfigs.forEach(
          new Consumer<VertexLoaderConfig>() {
            @Override
            public void accept(VertexLoaderConfig vertexLoaderConfig) {
              vertexLoaderConfig.setConnection(connection);
            }
          });
    }
    if (CollectionUtils.isNotEmpty(this.edgeLoaderConfigs)) {
      this.edgeLoaderConfigs.forEach(
          new Consumer<EdgeLoaderConfig>() {
            @Override
            public void accept(EdgeLoaderConfig edgeLoaderConfig) {
              edgeLoaderConfig.setConnection(connection);
            }
          });
    }
  }

  /**
   * Getter method for property <tt>schemaUrl</tt>.
   *
   * @return property value of schemaUrl
   */
  public String getSchemaUrl() {
    return schemaUrl;
  }

  /**
   * Setter method for property <tt>schemaUrl</tt>.
   *
   * @param schemaUrl value to be assigned to property schemaUrl
   */
  public void setSchemaUrl(String schemaUrl) {
    this.schemaUrl = schemaUrl;
  }

  /**
   * Getter method for property <tt>binary</tt>.
   *
   * @return property value of binary
   */
  public Boolean getBinary() {
    return binary;
  }

  /**
   * Setter method for property <tt>binary</tt>.
   *
   * @param binary value to be assigned to property binary
   */
  public void setBinary(Boolean binary) {
    this.binary = binary;
  }

  /** verify config */
  public GraphLoaderConfig verify() {
    if (CollectionUtils.isEmpty(this.edgeLoaderConfigs)) {
      this.vertexLoaderConfigs.forEach(vc -> vc.setAllowIsolateVertex(true));
    }

    if (null != this.edgeLoaderConfigs) {
      for (EdgeLoaderConfig edgeLoaderConfig : this.edgeLoaderConfigs) {
        if (edgeLoaderConfig.getEdgeTruncateThreshold() > this.edgeTruncateThreshold) {
          edgeLoaderConfig.setEdgeTruncateThreshold(edgeTruncateThreshold);
        }
      }
    }
    return this;
  }

  /**
   * Getter method for property <tt>taskId</tt>.
   *
   * @return property value of taskId
   */
  public String getTaskId() {
    return taskId;
  }

  /**
   * Setter method for property <tt>taskId</tt>.
   *
   * @param taskId value to be assigned to property schemaUrl
   */
  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  @Override
  public String toString() {
    StringBuilder str =
        new StringBuilder(
            "GraphLoaderConfig,schemaUrl="
                + this.schemaUrl
                + ",binary="
                + this.binary
                + ",edgeTruncateThreshold="
                + this.edgeTruncateThreshold);
    for (VertexLoaderConfig vertexLoaderConfig : this.vertexLoaderConfigs) {
      str.append(",[").append(vertexLoaderConfig).append("]");
    }
    for (EdgeLoaderConfig edgeLoaderConfig : this.edgeLoaderConfigs) {
      str.append(",[").append(edgeLoaderConfig).append("]");
    }
    return str.toString();
  }

  /**
   * Getter method for property <tt>graphVersionConfig</tt>.
   *
   * @return property value of graphVersionConfig
   */
  public GraphVersionConfig getGraphVersionConfig() {
    return graphVersionConfig;
  }

  /**
   * Setter method for property <tt>graphVersionConfig</tt>.
   *
   * @param graphVersionConfig value to be assigned to property graphVersionConfig
   */
  public void setGraphVersionConfig(GraphVersionConfig graphVersionConfig) {
    this.graphVersionConfig = graphVersionConfig;
  }
}
