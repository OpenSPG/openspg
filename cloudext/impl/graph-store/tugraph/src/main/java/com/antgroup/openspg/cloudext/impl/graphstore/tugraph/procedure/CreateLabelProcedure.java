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
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AddPropertyOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.SchemaAtomicOperationEnum;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/** Create label procedure. */
public class CreateLabelProcedure extends BaseTuGraphProcedure {

  /** The template of cypher */
  private static final String CREATE_LABEL_CYPHER_TEMPLATE =
      "CALL db.createLabel('${labelType}', '${labelName}', '${extra}'${fieldSpec});";

  /**
   * Type of the label
   *
   * <p>Either "vertex" and "edge"
   */
  private final String labelType;

  /** Name of the label */
  private final String labelName;

  /**
   * Extra
   *
   * <p>For edge, it means constraints; for vertex, it means primary property
   */
  private final String extra;

  /** Specification of a field */
  private final String fieldSpec;

  /** Constructor. */
  private CreateLabelProcedure(
      String cypherTemplate, String labelType, String labelName, String extra, String fieldSpec) {
    super(cypherTemplate);
    this.labelType = labelType;
    this.labelName = labelName;
    this.extra = extra;
    this.fieldSpec = fieldSpec;
  }

  public static CreateLabelProcedure of(CreateVertexTypeOperation createVertexTypeOperation) {
    List<AddPropertyOperation> addPropertyOperations =
        createVertexTypeOperation.getAtomicOperations().stream()
            .filter(
                atomicOperation ->
                    SchemaAtomicOperationEnum.ADD_PROPERTY.equals(
                        atomicOperation.getOperationTypeEnum()))
            .map(atomicOperation -> (AddPropertyOperation) atomicOperation)
            .collect(Collectors.toList());
    CreateLabelProcedure procedure =
        new CreateLabelProcedure(
            CREATE_LABEL_CYPHER_TEMPLATE,
            TuGraphConstants.LABEL_TYPE_VERTEX,
            createVertexTypeOperation.getVertexTypeName(),
            VertexType.ID,
            getLabelFieldSpecOfProperty(addPropertyOperations));
    return procedure;
  }

  public static CreateLabelProcedure of(CreateEdgeTypeOperation createEdgeTypeOperation) {
    List<AddPropertyOperation> addPropertyOperations =
        createEdgeTypeOperation.getAtomicOperations().stream()
            .filter(
                atomicOperation ->
                    SchemaAtomicOperationEnum.ADD_PROPERTY.equals(
                        atomicOperation.getOperationTypeEnum()))
            .map(atomicOperation -> (AddPropertyOperation) atomicOperation)
            .collect(Collectors.toList());
    CreateLabelProcedure procedure =
        new CreateLabelProcedure(
            CREATE_LABEL_CYPHER_TEMPLATE,
            TuGraphConstants.LABEL_TYPE_EDGE,
            createEdgeTypeOperation.getEdgeTypeName().getEdgeLabel(),
            String.format(
                "[[\"%s\",\"%s\"]]",
                createEdgeTypeOperation.getEdgeTypeName().getStartVertexType(),
                createEdgeTypeOperation.getEdgeTypeName().getEndVertexType()),
            getLabelFieldSpecOfProperty(addPropertyOperations));
    return procedure;
  }

  private static String getLabelFieldSpecOfProperty(
      List<AddPropertyOperation> addPropertyOperations) {
    if (CollectionUtils.isEmpty(addPropertyOperations)) {
      return StringUtils.EMPTY;
    }
    StringBuilder fieldSpecBuilder = new StringBuilder();
    for (AddPropertyOperation addPropertyOperation : addPropertyOperations) {
      LPGProperty lpgProperty = addPropertyOperation.getProperty();
      fieldSpecBuilder.append(
          String.format(
              ", ['%s',%s,%s]",
              lpgProperty.getName(),
              TuGraphSchemaConvertor.toTuGraphDataType(lpgProperty.getType()).getLowercaseForm(),
              lpgProperty.isOptional()));
    }
    return fieldSpecBuilder.toString();
  }

  @Override
  public String toString() {
    return "{\"procedure\":\"CreateLabelProcedure\", "
        + "\"labelType\":\""
        + labelType
        + "\", "
        + "\"labelName\":\""
        + labelName
        + "\", "
        + "\"extra\":\""
        + extra
        + "\", "
        + "\"fieldSpec\":\""
        + fieldSpec
        + "\", "
        + "\"cypherTemplate\":\""
        + getCypherTemplate()
        + "\"}";
  }
}
