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
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.convertor.TuGraphSchemaConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGProperty;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AddPropertyOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.SchemaAtomicOperationEnum;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class AlterLabelAddFieldsProcedure extends BaseTuGraphProcedure {

  /** The template of cypher */
  public static final String ALTER_LABEL_ADD_FIELDS_CYPHER_TEMPLATE =
      "CALL db.alterLabelAddFields('${labelType}', '${labelName}', ${fieldSpec})";

  /**
   * Type of the label
   *
   * <p>Either 'vertex' and 'edge'
   */
  private final String labelType;

  /** Name of the label */
  private final String labelName;

  /** Specification of a field */
  private final String fieldSpec;

  /** Constructor. */
  private AlterLabelAddFieldsProcedure(
      String cypherTemplate, String labelType, String labelName, String fieldSpec) {
    super(cypherTemplate);
    this.labelType = labelType;
    this.labelName = labelName;
    this.fieldSpec = fieldSpec;
  }

  /**
   * Alter label add fields procedure of alter edge type operation.
   *
   * @param alterEdgeTypeOperation the operation to alter edge type
   * @return the procedure to add property for edge type
   */
  public static AlterLabelAddFieldsProcedure of(AlterEdgeTypeOperation alterEdgeTypeOperation) {
    List<AddPropertyOperation> addPropertyOperations =
        alterEdgeTypeOperation.getAtomicOperations().stream()
            .filter(
                atomicOperation ->
                    SchemaAtomicOperationEnum.ADD_PROPERTY.equals(
                        atomicOperation.getOperationTypeEnum()))
            .map(atomicOperation -> (AddPropertyOperation) atomicOperation)
            .collect(Collectors.toList());
    return CollectionUtils.isEmpty(addPropertyOperations)
        ? new AlterLabelAddFieldsProcedure(
            TuGraphConstants.SCRIPT_NO_ALTER_TO_SCHEMA,
            TuGraphConstants.LABEL_TYPE_EDGE,
            alterEdgeTypeOperation.getEdgeTypeName().getEdgeLabel(),
            "[]")
        : new AlterLabelAddFieldsProcedure(
            ALTER_LABEL_ADD_FIELDS_CYPHER_TEMPLATE,
            TuGraphConstants.LABEL_TYPE_EDGE,
            alterEdgeTypeOperation.getEdgeTypeName().getEdgeLabel(),
            getFieldSpec(addPropertyOperations));
  }

  /**
   * Alter label add fields procedure of alter edge type operation.
   *
   * @param alterVertexTypeOperation the operation to alter vertex type
   * @return the procedure to add property for vertex type
   */
  public static AlterLabelAddFieldsProcedure of(AlterVertexTypeOperation alterVertexTypeOperation) {
    List<AddPropertyOperation> addPropertyOperations =
        alterVertexTypeOperation.getAtomicOperations().stream()
            .filter(
                atomicOperation ->
                    SchemaAtomicOperationEnum.ADD_PROPERTY.equals(
                        atomicOperation.getOperationTypeEnum()))
            .map(atomicOperation -> (AddPropertyOperation) atomicOperation)
            .collect(Collectors.toList());
    return CollectionUtils.isEmpty(addPropertyOperations)
        ? new AlterLabelAddFieldsProcedure(
            TuGraphConstants.SCRIPT_NO_ALTER_TO_SCHEMA,
            TuGraphConstants.LABEL_TYPE_VERTEX,
            alterVertexTypeOperation.getVertexTypeName(),
            "[]")
        : new AlterLabelAddFieldsProcedure(
            ALTER_LABEL_ADD_FIELDS_CYPHER_TEMPLATE,
            TuGraphConstants.LABEL_TYPE_VERTEX,
            alterVertexTypeOperation.getVertexTypeName(),
            getFieldSpec(addPropertyOperations));
  }

  private static String getFieldSpec(List<AddPropertyOperation> addPropertyOperations) {
    if (CollectionUtils.isEmpty(addPropertyOperations)) {
      throw new IllegalArgumentException(
          "add property operation list is empty when get field spec from properties.");
    }
    StringBuilder fieldSpecBuilder = new StringBuilder();
    for (AddPropertyOperation addPropertyOperation : addPropertyOperations) {
      if (fieldSpecBuilder.length() > 0) {
        fieldSpecBuilder.append(", ");
      }
      fieldSpecBuilder.append(getFieldString(addPropertyOperation.getProperty()));
    }
    return fieldSpecBuilder.toString();
  }

  private static String getFieldString(LPGProperty lpgProperty) {
    return "['"
        + lpgProperty.getName()
        + "',"
        + TuGraphSchemaConvertor.toTuGraphDataType(lpgProperty.getType()).getLowercaseForm()
        + ",null,true]";
  }
}
