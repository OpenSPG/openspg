package com.antgroup.openspg.builder.core.runtime.impl;

import com.antgroup.openspg.builder.core.runtime.BuilderCatalog;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ConceptList;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
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
}
