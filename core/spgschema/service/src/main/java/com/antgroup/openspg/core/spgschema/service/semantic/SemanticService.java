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

package com.antgroup.openspg.core.spgschema.service.semantic;

import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;

import java.util.List;

/**
 * The domain interface of the predicate semantic model, providing methods for adding, modifying, deleting, and querying
 * predicate semantics.
 */
public interface SemanticService {

    /**
     * Query semantics of property or relation.
     *
     * @param subjectIds   list of subject id, such as unique ids of property or relation.
     * @param ontologyEnum ontology type
     * @return list of semantic
     */
    List<PredicateSemantic> queryBySubjectIds(List<Long> subjectIds, SPGOntologyEnum ontologyEnum);

    /**
     * Create or update a semantic
     *
     * @param predicateSemantic semantic model
     * @return record count is added
     */
    int saveOrUpdate(PredicateSemantic predicateSemantic);

    /**
     * Delete semantic by spo triple key
     *
     * @param predicateSemantic semantic model
     * @return record count is deleted
     */
    int delete(PredicateSemantic predicateSemantic);
}
