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

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.lube.catalog.AbstractConnection;
import com.antgroup.openspg.reasoner.lube.common.rule.Rule;
import com.antgroup.openspg.reasoner.warehouse.utils.WareHouseUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;

public class EdgeLoaderConfig implements Serializable {
  /** edge type */
  private String edgeType;

  /** maximum edge threshold for this edge type */
  private int edgeTruncateThreshold = GraphLoaderConfig.DEFAULT_EDGE_TRUNCATE_THRESHOLD;

  /**
   * connection info for this edge type considering that each edge may be stored in different
   * storage or different table
   */
  private AbstractConnection connection;

  /** cut out the unnecessary attribute fields, and only keep the necessary attributes here */
  private Set<String> needProperties;

  /** pushdown edge attribute filtering rules */
  private List<Rule> propertiesFilterRules;

  private List<Tuple2<String, List<String>>> propertiesFilterRuleString;

  /** edge load direction */
  private Direction loadDirection = Direction.BOTH;

  /**
   * get start type from spo format
   *
   * @return
   */
  public String getStartType() {
    String[] relation = StringUtils.split(this.edgeType, '_');
    if (relation == null || relation.length < 3) {
      throw new RuntimeException("RelationData parse spo content " + this.edgeType + "  failed!");
    }
    return relation[0];
  }

  /**
   * get end type from spo format
   *
   * @return
   */
  public String getEndType() {
    String[] relation = StringUtils.split(this.edgeType, '_');
    if (relation == null || relation.length < 3) {
      throw new RuntimeException("RelationData parse spo content " + this.edgeType + "  failed!");
    }
    return relation[2];
  }

  /**
   * get edge type
   *
   * @return
   */
  public String getEdgeType() {
    return edgeType;
  }

  /**
   * setter
   *
   * @param edgeType
   */
  public void setEdgeType(String edgeType) {
    this.edgeType = edgeType;
  }

  /**
   * getter
   *
   * @return
   */
  public int getEdgeTruncateThreshold() {
    return edgeTruncateThreshold;
  }

  /**
   * setter
   *
   * @param edgeTruncateThreshold
   */
  public void setEdgeTruncateThreshold(int edgeTruncateThreshold) {
    this.edgeTruncateThreshold = edgeTruncateThreshold;
  }

  /**
   * getter
   *
   * @return
   */
  public Set<String> getNeedProperties() {
    return needProperties == null ? Collections.emptySet() : needProperties;
  }

  /**
   * setter
   *
   * @param needProperties
   */
  public void setNeedProperties(Set<String> needProperties) {
    this.needProperties = needProperties;
  }

  /**
   * getter
   *
   * @return
   */
  public List<Rule> getPropertiesFilterRules() {
    return propertiesFilterRules == null ? Collections.emptyList() : propertiesFilterRules;
  }

  /**
   * get properties filter rule
   *
   * @return
   */
  public List<Tuple2<String, List<String>>> getPropertiesFilterRuleString() {
    if (null != propertiesFilterRuleString) {
      return propertiesFilterRuleString;
    }
    propertiesFilterRuleString = new ArrayList<>();
    for (Rule rule : getPropertiesFilterRules()) {
      propertiesFilterRuleString.add(WareHouseUtils.getRuleListWithAlias(rule));
    }
    return propertiesFilterRuleString;
  }

  /** merge */
  public EdgeLoaderConfig merge(EdgeLoaderConfig other) {
    Set<String> newProperties = new HashSet<>(this.getNeedProperties());
    if (CollectionUtils.isNotEmpty(other.getNeedProperties())) {
      newProperties.addAll(other.needProperties);
    }
    this.needProperties = newProperties;

    List<Rule> newFilterRules = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(this.propertiesFilterRules)) {
      newFilterRules.addAll(this.propertiesFilterRules);
    }
    if (CollectionUtils.isNotEmpty(other.propertiesFilterRules)) {
      newFilterRules.addAll(other.propertiesFilterRules);
    }
    this.setPropertiesFilterRules(newFilterRules);

    this.getPropertiesFilterRuleString();

    Direction newLoadDirection;
    if (Direction.BOTH.equals(this.loadDirection) || Direction.BOTH.equals(other.loadDirection)) {
      newLoadDirection = Direction.BOTH;
    } else if (Direction.IN.equals(this.loadDirection)
        && Direction.OUT.equals(other.loadDirection)) {
      newLoadDirection = Direction.BOTH;
    } else if (Direction.OUT.equals(this.loadDirection)
        && Direction.IN.equals(other.loadDirection)) {
      newLoadDirection = Direction.BOTH;
    } else {
      newLoadDirection = this.loadDirection;
    }
    this.setLoadDirection(newLoadDirection);
    return this;
  }

  public void setPropertiesFilterRules(List<Rule> propertiesFilterRules) {
    this.propertiesFilterRules = propertiesFilterRules;
    this.propertiesFilterRuleString = null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EdgeLoaderConfig)) {
      return false;
    }
    EdgeLoaderConfig that = (EdgeLoaderConfig) o;
    return Objects.equals(edgeType, that.edgeType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(edgeType);
  }

  /**
   * Getter method for property connection.
   *
   * @return property value of connection
   */
  public AbstractConnection getConnection() {
    return connection;
  }

  /**
   * Setter method for property connection.
   *
   * @param connection value to be assigned to property connection
   */
  public void setConnection(AbstractConnection connection) {
    this.connection = connection;
  }

  /**
   * Getter method for property <tt>loadDirection</tt>.
   *
   * @return property value of loadDirection
   */
  public Direction getLoadDirection() {
    return loadDirection;
  }

  /**
   * Setter method for property <tt>loadDirection</tt>.
   *
   * @param loadDirection value to be assigned to property loadDirection
   */
  public void setLoadDirection(Direction loadDirection) {
    this.loadDirection = loadDirection;
  }

  @Override
  public String toString() {
    return "EdgeLoaderConfig,edgeType="
        + this.edgeType
        + ",edgeTruncateThreshold="
        + this.edgeTruncateThreshold
        + ",connection="
        + this.connection
        + ",needProperties="
        + this.needProperties
        + ",propertiesFilterRuleString="
        + this.getPropertiesFilterRuleString()
        + ",loadDirection="
        + this.getLoadDirection();
  }
}
