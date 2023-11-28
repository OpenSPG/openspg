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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.convertor;

import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model.DataTypeEnum;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model.TuGraphEdgeType;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model.TuGraphProperty;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model.TuGraphVertexType;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.result.GetEdgeSchemaResult;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.result.GetVertexSchemaResult;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.result.QueryLabelsResult;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGProperty;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType;
import com.antgroup.openspg.core.schema.model.type.BasicTypeEnum;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class TuGraphSchemaConvertor {

  public static List<String> toLabels(List<QueryLabelsResult> labelQueryResult) {
    if (CollectionUtils.isEmpty(labelQueryResult)) {
      return Lists.newArrayList();
    } else {
      return labelQueryResult.stream()
          .filter(Objects::nonNull)
          .map(QueryLabelsResult::getLabelName)
          .collect(Collectors.toList());
    }
  }

  public static VertexType toVertexType(GetVertexSchemaResult result) {
    if (result == null || result.getSchema() == null) {
      return null;
    }
    TuGraphVertexType tuGraphVertexSchema = result.getSchema();
    return new VertexType(
        tuGraphVertexSchema.getLabel(), toLpgProperty(tuGraphVertexSchema.getProperties()));
  }

  public static EdgeType toEdgeType(GetEdgeSchemaResult result) {
    if (result == null || result.getSchema() == null) {
      return null;
    }
    TuGraphEdgeType tuGraphEdgeSchema = result.getSchema();
    List<String> vertexPairArray = tuGraphEdgeSchema.getConstraints().get(0);
    return new EdgeType(
        new EdgeTypeName(
            vertexPairArray.get(0), tuGraphEdgeSchema.getLabel(), vertexPairArray.get(1)),
        toLpgProperty(tuGraphEdgeSchema.getProperties()));
  }

  private static List<LPGProperty> toLpgProperty(List<TuGraphProperty> tuGraphProperties) {
    return tuGraphProperties.stream()
        .map(
            tuGraphProperty ->
                new LPGProperty(tuGraphProperty.getName(), toBasicType(tuGraphProperty.getType())))
        .collect(Collectors.toList());
  }

  private static BasicTypeEnum toBasicType(DataTypeEnum type) {
    switch (type) {
      case INT8:
      case INT16:
      case INT32:
      case INT64:
        return BasicTypeEnum.LONG;
      case DATE:
      case DATETIME:
      case STRING:
        return BasicTypeEnum.TEXT;
      case FLOAT:
      case DOUBLE:
        return BasicTypeEnum.DOUBLE;
      default:
        throw new RuntimeException(
            "Unexpected type when converting to cloud-sdk attribute, type=" + type);
    }
  }

  public static DataTypeEnum toTuGraphDataType(BasicTypeEnum basicType) {
    switch (basicType) {
      case TEXT:
        return DataTypeEnum.STRING;
      case LONG:
        return DataTypeEnum.INT64;
      case DOUBLE:
        return DataTypeEnum.DOUBLE;
      default:
        throw new RuntimeException(
            "Unexpected attribute when converting to tugraph property type, attribute="
                + basicType);
    }
  }
}
