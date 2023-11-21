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

package com.antgroup.openspg.core.spgschema.model;

import com.antgroup.openspg.common.model.base.BaseToString;
import com.antgroup.openspg.core.spgschema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.spgschema.model.type.WithAlterOperation;
import com.antgroup.openspg.core.spgschema.model.type.WithOntologyId;

/**
 * The abstract parent class of all ontology models<br>
 *
 * <p>In the RDF framework, knowledge is represented by a triple of <subject, predicate, object>.
 * The subject and object can be entities, concepts, events, or constants such as text and numbers.
 * The predicate can be properties or relations. These combinations are referred to as ontologies.
 * Ontology, originating from philosophy, is a concept that denotes "a formalized, clear, and
 * detailed description of a shared concept system". It is used to guide cognitive modeling of
 * entities, terms, and concepts within a specific domain in the real world, as well as to define
 * the schema of knowledge graph.
 */
public class BaseOntology extends BaseToString implements WithOntologyId, WithAlterOperation {

  private static final long serialVersionUID = -8518344123563921268L;

  /** The project id. */
  private Long projectId;

  /** Ontology id. */
  private OntologyId ontologyId;

  /** The operator of this alter. */
  private AlterOperationEnum alterOperation;

  /** Extend information. */
  private SchemaExtInfo extInfo;

  @Override
  public OntologyId getOntologyId() {
    return ontologyId;
  }

  public void setOntologyId(OntologyId ontologyId) {
    this.ontologyId = ontologyId;
  }

  public AlterOperationEnum getAlterOperation() {
    return alterOperation;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public SchemaExtInfo getExtInfo() {
    return extInfo;
  }

  public Object getBooleanExtInfo(String key) {
    return extInfo == null ? null : extInfo.getBoolean(key);
  }

  public Object getStringExtInfo(String key) {
    return extInfo == null ? null : extInfo.getString(key);
  }

  public Object getLongExtInfo(String key) {
    return extInfo == null ? null : extInfo.getLong(key);
  }

  public void setExtInfo(SchemaExtInfo extInfo) {
    this.extInfo = extInfo;
  }

  public void setAlterOperation(AlterOperationEnum alterOperation) {
    this.alterOperation = alterOperation;
  }

  public void addExtConfig(String key, Object val) {
    if (extInfo == null) {
      extInfo = new SchemaExtInfo();
    }
    extInfo.put(key, val);
  }
}
