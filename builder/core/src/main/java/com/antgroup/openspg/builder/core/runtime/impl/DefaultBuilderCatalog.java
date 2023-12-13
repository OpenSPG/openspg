package com.antgroup.openspg.builder.core.runtime.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderCatalog;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ConceptList;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.server.common.model.datasource.connection.SearchEngineConnectionInfo;
import java.util.HashMap;
import java.util.Map;

public class DefaultBuilderCatalog implements BuilderCatalog {

  private final ProjectSchema projectSchema;
  private final Map<SPGTypeIdentifier, ConceptList> conceptLists;

  public DefaultBuilderCatalog(
      ProjectSchema projectSchema, Map<SPGTypeIdentifier, ConceptList> conceptLists) {
    this.projectSchema = projectSchema;
    this.conceptLists = conceptLists;
  }

  @Override
  public ProjectSchema getProjectSchema() {
    return projectSchema;
  }

  @Override
  public boolean isSpreadable(SPGTypeIdentifier identifier) {
    return projectSchema.getSpreadable(identifier);
  }

  @Override
  public BaseSPGType getSPGType(SPGTypeIdentifier identifier) {
    return projectSchema.getByName(identifier);
  }

  @Override
  public Relation getRelation(RelationIdentifier identifier) {
    return projectSchema.getByName(identifier);
  }

  @Override
  public ConceptList getConceptList(SPGTypeIdentifier conceptType) {
    return conceptLists.get(conceptType);
  }

  @Override
  public GraphStoreConnectionInfo getGraphStoreConnInfo() {
    // "graphstore:tugraph://127.0.0.1:9090/default?timeout=60000&accessId=admin&accessKey=73@TuGraph";
    Map<String, Object> params = new HashMap<>();
    params.put("graphName", "default");
    params.put("timeout", "60000");
    params.put("accessId", "admin");
    params.put("accessKey", "73@TuGraph");
    params.put("host", "127.0.0.1:9090");

    return new GraphStoreConnectionInfo().setScheme("tugraph").setParams(params);
  }

  @Override
  public SearchEngineConnectionInfo getSearchEngineConnInfo() {
    Map<String, Object> params = new HashMap<>();
    params.put("host", "127.0.0.1");
    params.put("port", "9200");
    params.put("scheme", "http");

    return new SearchEngineConnectionInfo().setScheme("elasticsearch").setParams(params);
  }
}
