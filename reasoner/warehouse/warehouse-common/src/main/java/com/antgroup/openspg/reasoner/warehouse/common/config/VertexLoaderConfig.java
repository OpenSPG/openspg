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

package com.antgroup.openspg.reasoner.warehouse.common.config;

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
import scala.Tuple2;

public class VertexLoaderConfig implements Serializable {
  /** vertex type */
  private String vertexType;

  /**
   * connection info for this vertex type considering that each vertex may be stored in different
   * storage or different table
   */
  private Set<AbstractConnection> connection;

  /** cut out the unnecessary attribute fields, and only keep the necessary attributes here */
  private Set<String> needProperties;

  /** pushdown vertex attribute filtering rules */
  private List<Rule> propertiesFilterRules;

  private List<Tuple2<String, List<String>>> propertiesFilterRuleString;

  /** allow loading of isolate vertex */
  private boolean allowIsolateVertex = false;
  /** if this vertex is a concept, give the hypernym for loader all data */
  private String conceptHypernym = null;

  public String getVertexType() {
    return vertexType;
  }

  public void setVertexType(String vertexType) {
    this.vertexType = vertexType;
  }

  public Set<String> getNeedProperties() {
    return needProperties == null ? Collections.emptySet() : needProperties;
  }

  public void setNeedProperties(Set<String> needProperties) {
    this.needProperties = needProperties;
  }

  public List<Rule> getPropertiesFilterRules() {
    return propertiesFilterRules == null ? Collections.emptyList() : propertiesFilterRules;
  }

  public void setPropertiesFilterRules(List<Rule> propertiesFilterRules) {
    this.propertiesFilterRules = propertiesFilterRules;
    this.propertiesFilterRuleString = null;
  }

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
  public VertexLoaderConfig merge(VertexLoaderConfig other) {
    Set<String> newProperties = new HashSet<>(getNeedProperties());
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

    this.allowIsolateVertex = this.allowIsolateVertex || other.allowIsolateVertex;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VertexLoaderConfig)) {
      return false;
    }
    VertexLoaderConfig that = (VertexLoaderConfig) o;
    return Objects.equals(vertexType, that.vertexType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vertexType);
  }

  /**
   * Getter method for property connection.
   *
   * @return property value of connection
   */
  public Set<AbstractConnection> getConnection() {
    return connection;
  }

  /**
   * Setter method for property connection.
   *
   * @param connection value to be assigned to property connection
   */
  public void setConnection(Set<AbstractConnection> connection) {
    this.connection = connection;
  }

  /**
   * Getter method for property <tt>allowIsolateVertex</tt>.
   *
   * @return property value of allowIsolateVertex
   */
  public boolean isAllowIsolateVertex() {
    return allowIsolateVertex;
  }

  /**
   * Setter method for property <tt>allowIsolateVertex</tt>.
   *
   * @param allowIsolateVertex value to be assigned to property allowIsolateVertex
   */
  public void setAllowIsolateVertex(boolean allowIsolateVertex) {
    this.allowIsolateVertex = allowIsolateVertex;
  }

  /**
   * Getter method for property <tt>conceptHypernym</tt>.
   *
   * @return property value of conceptHypernym
   */
  public String getConceptHypernym() {
    return conceptHypernym;
  }
  /**
   * Setter method for property <tt>conceptHypernym</tt>.
   *
   * @param conceptHypernym value to be assigned to property conceptHypernym
   */
  public void setConceptHypernym(String conceptHypernym) {
    this.conceptHypernym = conceptHypernym;
  }

  @Override
  public String toString() {
    return "VertexLoaderConfig,vertexType="
        + this.vertexType
        + ",connection="
        + this.connection
        + ",needProperties="
        + this.needProperties
        + ",allowIsolateVertex="
        + this.allowIsolateVertex
        + ",propertiesFilterRuleString="
        + this.getPropertiesFilterRuleString();
  }
}
