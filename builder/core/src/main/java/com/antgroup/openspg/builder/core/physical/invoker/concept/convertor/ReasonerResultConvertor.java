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

package com.antgroup.openspg.builder.core.physical.invoker.concept.convertor;

import com.antgroup.kg.reasoner.common.graph.edge.IEdge;
import com.antgroup.kg.reasoner.common.graph.property.IProperty;
import com.antgroup.kg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.kg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.kg.reasoner.local.model.LocalReasonerResult;
import com.antgroup.openspg.api.facade.client.SchemaFacade;
import com.antgroup.openspg.api.facade.dto.schema.request.RelationRequest;
import com.antgroup.openspg.api.facade.dto.schema.request.SPGTypeRequest;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.EdgeRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor.VertexRecordConvertor;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseSPGRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.RelationRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGPropertyRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGPropertyValue;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

public class ReasonerResultConvertor {

  public static void setBelongToProperty(
      LocalReasonerResult result, BaseAdvancedRecord advancedRecord) {
    if (CollectionUtils.isEmpty(result.getEdgeList())) {
      return;
    }

    Property belongToProperty =
        advancedRecord.getSpgType().getPredicateProperty(SystemPredicateEnum.BELONG_TO);
    if (belongToProperty == null) {
      throw new IllegalStateException(
          String.format("spgType=%s has not belongTo property", advancedRecord.getName()));
    }

    IEdge<IVertexId, IProperty> edge = result.getEdgeList().get(0);
    SPGPropertyRecord propertyRecord =
        new SPGPropertyRecord(
            belongToProperty, new SPGPropertyValue(edge.getTargetId().getBizId()));
    advancedRecord.mergePropertyValue(propertyRecord);
  }

  public static List<BaseSPGRecord> toSpgRecords(
      LocalReasonerResult result, SchemaFacade spgSchemaFacade) {
    List<IVertex<IVertexId, IProperty>> vertices =
        CollectionsUtils.defaultEmpty(result.getVertexList());
    List<IEdge<IVertexId, IProperty>> edges = CollectionsUtils.defaultEmpty(result.getEdgeList());

    List<BaseSPGRecord> results = new ArrayList<>(vertices.size() + edges.size());
    vertices.forEach(
        vertex -> {
          IVertexId vertexId = vertex.getId();
          Map<String, String> properties = toProps(vertex.getValue());
          BaseSPGType spgType =
              spgSchemaFacade
                  .querySPGType(new SPGTypeRequest().setName(vertexId.getType()))
                  .getDataThrowsIfNull(vertexId.getType());

          BaseAdvancedRecord advancedRecord =
              VertexRecordConvertor.toAdvancedRecord(spgType, vertexId.getBizId(), properties);
          results.add(advancedRecord);
        });

    edges.forEach(
        edge -> {
          Relation relationType =
              spgSchemaFacade
                  .queryRelation(RelationRequest.parse(edge.getType()))
                  .getDataThrowsIfNull(edge.getType());
          Map<String, String> properties = toProps(edge.getValue());

          RelationRecord relationRecord =
              EdgeRecordConvertor.toRelationRecord(
                  relationType,
                  edge.getSourceId().getBizId(),
                  edge.getTargetId().getBizId(),
                  properties);
          results.add(relationRecord);
        });
    return results;
  }

  private static Map<String, String> toProps(IProperty property) {
    Collection<String> keySet = property.getKeySet();

    Map<String, String> properties = new HashMap<>(keySet.size());
    for (String key : keySet) {
      Object value = property.get(key);
      if (value != null) {
        properties.put(key, value.toString());
      }
    }
    return properties;
  }
}
