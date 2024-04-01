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

package com.antgroup.openspg.reasoner.runner.local.model;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class LocalReasonerResult {

  private final boolean graphResult;

  private final String errMsg;

  // select result
  private final List<String> columns;
  private final List<Object[]> rows;

  // ddl result
  private final List<IVertex<IVertexId, IProperty>> vertexList;
  private final List<IEdge<IVertexId, IProperty>> edgeList;

  /**
   * return with err msg
   *
   * @param errMsg
   */
  public LocalReasonerResult(String errMsg) {
    this.columns = null;
    this.rows = null;
    this.graphResult = false;
    this.vertexList = new ArrayList<>();
    this.edgeList = new ArrayList<>();
    this.errMsg = errMsg;
  }
  /** select result */
  public LocalReasonerResult(List<String> columns, List<Object[]> rows) {
    this.columns = columns;
    this.rows = rows;
    this.graphResult = false;
    this.vertexList = null;
    this.edgeList = null;
    this.errMsg = "";
  }

  /** ddl result */
  public LocalReasonerResult(
      List<IVertex<IVertexId, IProperty>> vertexList,
      List<IEdge<IVertexId, IProperty>> edgeList,
      boolean graphResult) {
    this.columns = null;
    this.rows = null;
    this.graphResult = graphResult;
    this.vertexList = vertexList;
    this.edgeList = edgeList;
    this.errMsg = "";
  }

  /** output graph and row */
  public LocalReasonerResult(
      List<String> columns,
      List<Object[]> rows,
      List<IVertex<IVertexId, IProperty>> vertexList,
      List<IEdge<IVertexId, IProperty>> edgeList,
      boolean graphResult) {
    this.columns = columns;
    this.rows = rows;
    this.graphResult = graphResult;
    this.vertexList = vertexList;
    this.edgeList = edgeList;
    this.errMsg = "";
  }

  /**
   * Getter method for property <tt>ddlResult</tt>.
   *
   * @return property value of ddlResult
   */
  public boolean isGraphResult() {
    return graphResult;
  }

  /**
   * Getter method for property <tt>columns</tt>.
   *
   * @return property value of columns
   */
  public List<String> getColumns() {
    return columns;
  }

  /**
   * Getter method for property <tt>rows</tt>.
   *
   * @return property value of rows
   */
  public List<Object[]> getRows() {
    return rows;
  }

  /**
   * Getter method for property <tt>vertexList</tt>.
   *
   * @return property value of vertexList
   */
  public List<IVertex<IVertexId, IProperty>> getVertexList() {
    return vertexList;
  }

  /**
   * Getter method for property <tt>edgeList</tt>.
   *
   * @return property value of edgeList
   */
  public List<IEdge<IVertexId, IProperty>> getEdgeList() {
    return edgeList;
  }

  @Override
  public String toString() {
    if (graphResult) {
      return ddlToString();
    } else {
      return rowsToString();
    }
  }

  protected String rowsToString() {
    StringBuilder sb = new StringBuilder();
    assert this.columns != null;
    sb.append("columns:").append(String.join(",", this.getColumns()));
    for (int i = 0; i < rows.size(); ++i) {
      Object[] row = rows.get(i);
      sb.append("\n");
      sb.append("(").append(i).append(") ").append(Arrays.toString(row));
    }
    return sb.toString();
  }

  public String getErrMsg() {
    return errMsg;
  }

  protected String ddlToString() {
    StringBuilder sb = new StringBuilder();
    if (CollectionUtils.isNotEmpty(this.vertexList)) {
      sb.append("vertex_list_start\n");
      for (int i = 0; i < this.vertexList.size(); ++i) {
        IVertex<IVertexId, IProperty> vertex = this.getVertexList().get(i);
        sb.append("(").append(i).append(") ").append(vertex).append("\n");
      }
      sb.append("vertex_list_end");
    }
    if (CollectionUtils.isNotEmpty(this.edgeList)) {
      if (sb.length() > 0) {
        sb.append("\n");
      }
      sb.append("edge_list_start\n");
      for (int i = 0; i < this.edgeList.size(); ++i) {
        IEdge<IVertexId, IProperty> edge = this.getEdgeList().get(i);
        sb.append("(").append(i).append(") ").append(edge).append("\n");
      }
      sb.append("edge_list_end");
    }
    return sb.toString();
  }
}
