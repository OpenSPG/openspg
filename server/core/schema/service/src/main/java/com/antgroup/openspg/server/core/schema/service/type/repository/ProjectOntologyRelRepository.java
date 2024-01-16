/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.server.core.schema.service.type.repository;

import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.server.core.schema.service.type.model.ProjectOntologyRel;
import java.util.List;

/**
 * The read-write interface for project and ontology relationship, manages the definition and
 * reference relationship between projects and {@link BaseSPGType} or {@link Relation}, and provides
 * methods for adding, deleting, and querying project ontology relationships.
 */
public interface ProjectOntologyRelRepository {

  /**
   * Save the definition or reference relationship between the project and {@link BaseSPGType} or
   * {@link Relation}
   *
   * @param projectOntologyRel relationship
   * @return record count
   */
  int save(ProjectOntologyRel projectOntologyRel);

  /**
   * Delete the definition or reference relationship between the project and {@link BaseSPGType} or
   * {@link Relation}
   *
   * @param uniqueId unique id of relationship
   * @return record count
   */
  int delete(Long uniqueId);

  /**
   * Query the relationship by project id.
   *
   * @param projectId project id
   * @return list of relationship between the project and {@link BaseSPGType} or {@link Relation}
   */
  List<ProjectOntologyRel> queryByProjectId(Long projectId);

  /**
   * Query the relationship by ontology id and ontology type
   *
   * @param uniqueId unique id of spg type
   * @param ontologyEnum spg type or relation type
   * @return relationship between the project and {@link BaseSPGType} or {@link Relation}
   */
  ProjectOntologyRel queryByOntologyId(Long uniqueId, SPGOntologyEnum ontologyEnum);

  /**
   * Batch Query the relationship by ontology id and ontology type
   *
   * @param uniqueIds list of ontology id
   * @param ontologyEnum spg type or relation type
   * @return list of relationship
   */
  List<ProjectOntologyRel> queryByOntologyId(List<Long> uniqueIds, SPGOntologyEnum ontologyEnum);
}
