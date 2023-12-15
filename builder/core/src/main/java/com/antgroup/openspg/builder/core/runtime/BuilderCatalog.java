package com.antgroup.openspg.builder.core.runtime;

import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ConceptList;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.server.common.model.datasource.connection.SearchEngineConnectionInfo;
import java.io.Serializable;

public interface BuilderCatalog extends Serializable {

  ProjectSchema getProjectSchema();

  boolean isSpreadable(SPGTypeIdentifier identifier);

  BaseSPGType getSPGType(SPGTypeIdentifier identifier);

  Relation getRelation(RelationIdentifier identifier);

  ConceptList getConceptList(SPGTypeIdentifier conceptType);

  SearchEngineConnectionInfo getSearchEngineConnInfo();

  GraphStoreConnectionInfo getGraphStoreConnInfo();
}
