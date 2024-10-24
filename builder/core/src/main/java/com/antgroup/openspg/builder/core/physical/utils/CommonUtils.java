package com.antgroup.openspg.builder.core.physical.utils;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.PipelineConfigException;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.RelationRecord;
import com.antgroup.openspg.builder.model.record.SPGRecordTypeEnum;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.EdgeRecordConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util.VertexRecordConvertor;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.server.api.facade.ApiResponse;
import com.antgroup.openspg.server.api.facade.client.SchemaFacade;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.server.api.http.client.HttpSchemaFacade;
import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class CommonUtils {

  private static final String DOT = ".";

  private static final SPGTypeRef TEXT_REF =
      new SPGTypeRef(new BasicInfo<>(SPGTypeIdentifier.parse("Text")), SPGTypeEnum.BASIC_TYPE);

  public static ProjectSchema getProjectSchema(BuilderContext context) {
    HttpClientBootstrap.init(
        new ConnectionInfo(context.getSchemaUrl()).setConnectTimeout(6000).setReadTimeout(600000));

    SchemaFacade schemaFacade = new HttpSchemaFacade();
    ApiResponse<ProjectSchema> response =
        schemaFacade.queryProjectSchema(new ProjectSchemaRequest(context.getProjectId()));
    if (response.isSuccess()) {
      return response.getData();
    }
    throw new PipelineConfigException(
        "get schema error={}, schemaUrl={}, projectId={}",
        response.getErrorMsg(),
        context.getSchemaUrl(),
        context.getProjectId());
  }

  public static List<BaseSPGRecord> convertNodes(
      SubGraphRecord subGraph, ProjectSchema projectSchema, String namespace) {
    List<SubGraphRecord.Node> resultNodes = subGraph.getResultNodes();
    List<BaseSPGRecord> records = new ArrayList<>();
    if (CollectionUtils.isEmpty(resultNodes)) {
      return records;
    }
    for (SubGraphRecord.Node node : resultNodes) {
      BaseSPGType spgType = projectSchema.getByName(labelPrefix(namespace, node.getLabel()));
      if (spgType == null) {
        continue;
      }
      Map<String, String> stringMap =
          node.getProperties().entrySet().stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      entry -> entry.getValue() == null ? null : entry.getValue().toString()));
      BaseSPGRecord baseSPGRecord =
          VertexRecordConvertor.toAdvancedRecord(spgType, String.valueOf(node.getId()), stringMap);
      records.add(baseSPGRecord);
    }
    records.forEach(CommonUtils::replaceUnSpreadableStandardProperty);
    return records;
  }

  private static String labelPrefix(String namespace, String label) {
    if (label.contains(DOT)) {
      return label;
    }
    return namespace + DOT + label;
  }

  public static List<BaseSPGRecord> convertEdges(
      SubGraphRecord subGraph, ProjectSchema projectSchema, String namespace) {
    List<SubGraphRecord.Edge> resultEdges = subGraph.getResultEdges();
    List<BaseSPGRecord> records = new ArrayList<>();
    if (CollectionUtils.isEmpty(resultEdges)) {
      return records;
    }
    for (SubGraphRecord.Edge edge : resultEdges) {
      RelationIdentifier identifier =
          RelationIdentifier.parse(
              labelPrefix(namespace, edge.getFromType())
                  + '_'
                  + edge.getLabel()
                  + '_'
                  + labelPrefix(namespace, edge.getToType()));
      Relation relation = projectSchema.getByName(identifier);
      if (relation == null) {
        continue;
      }
      Map<String, String> stringMap =
          edge.getProperties().entrySet().stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      entry -> entry.getValue() == null ? null : entry.getValue().toString()));
      RelationRecord relationRecord =
          EdgeRecordConvertor.toRelationRecord(
              relation, String.valueOf(edge.getFrom()), String.valueOf(edge.getTo()), stringMap);
      records.add(relationRecord);
    }
    return records;
  }

  private static void replaceUnSpreadableStandardProperty(BaseSPGRecord record) {
    if (SPGRecordTypeEnum.RELATION.equals(record.getRecordType())) {
      return;
    }

    record
        .getProperties()
        .forEach(
            property -> {
              Property propertyType = ((SPGPropertyRecord) property).getProperty();
              propertyType.setObjectTypeRef(TEXT_REF);
              property.getValue().setSingleStd(property.getValue().getRaw());
            });
  }
}
