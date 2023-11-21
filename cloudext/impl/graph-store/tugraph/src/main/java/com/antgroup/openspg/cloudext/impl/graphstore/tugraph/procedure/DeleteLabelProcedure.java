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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure;

import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.TuGraphConstants;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropVertexTypeOperation;

public class DeleteLabelProcedure extends BaseTuGraphProcedure {

  /** The template of cypher */
  private static final String DELETE_LABEL_CYPHER_TEMPLATE =
      "CALL db.deleteLabel('${labelType}', '${labelName}');";

  /** Type of label */
  private final String labelType;

  /** Name of label */
  private final String labelName;

  /** Constructor */
  private DeleteLabelProcedure(String cypherTemplate, String labelType, String labelName) {
    super(cypherTemplate);
    this.labelType = labelType;
    this.labelName = labelName;
  }

  /**
   * Delete label procedure of delete vertex type operation.
   *
   * @param dropVertexTypeOperation the operation to drop vertex type.
   * @return the procedure to delete vertex type
   */
  public static DeleteLabelProcedure of(DropVertexTypeOperation dropVertexTypeOperation) {
    return new DeleteLabelProcedure(
        DELETE_LABEL_CYPHER_TEMPLATE,
        TuGraphConstants.LABEL_TYPE_VERTEX,
        dropVertexTypeOperation.getVertexTypeName());
  }

  /**
   * Delete label procedure of delete edge type operation.
   *
   * @param dropEdgeTypeOperation the operation to drop edge type
   * @return the procedure to delete edge type
   */
  public static DeleteLabelProcedure of(DropEdgeTypeOperation dropEdgeTypeOperation) {
    return new DeleteLabelProcedure(
        DELETE_LABEL_CYPHER_TEMPLATE,
        TuGraphConstants.LABEL_TYPE_EDGE,
        dropEdgeTypeOperation.getEdgeTypeName().getEdgeLabel());
  }

  @Override
  public String toString() {
    return "{\"procedure\":\"DeleteLabelProcedure\", "
        + "\"labelType\":\""
        + labelType
        + "\", "
        + "\"labelName\":\""
        + labelName
        + "\", "
        + "\"cypherTemplate\":\""
        + getCypherTemplate()
        + "\"}";
  }
}
