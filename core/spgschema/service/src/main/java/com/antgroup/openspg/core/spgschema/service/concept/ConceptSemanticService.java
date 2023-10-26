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

package com.antgroup.openspg.core.spgschema.service.concept;

import com.antgroup.openspg.core.spgschema.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.core.spgschema.model.semantic.LogicalCausationSemantic;
import com.antgroup.openspg.core.spgschema.service.semantic.model.LogicalCausationQuery;

import java.util.List;

/**
 * The domain interface of concept semantics, provides methods for adding, modifying, deleting, and querying concepts'
 * belongTo semantics and relational semantics.
 */
public interface ConceptSemanticService {

    /**
     * Query dynamic taxonomic semantic by condition.
     *
     * @param conceptTypeIdentifier the type name of concept
     * @param conceptIdentifier     the unique name of concept
     * @return belong to semantic
     */
    List<DynamicTaxonomySemantic> queryDynamicTaxonomySemantic(
        SPGTypeIdentifier conceptTypeIdentifier, ConceptIdentifier conceptIdentifier);

    /**
     * Delete the semantic record of belong to concept
     *
     * @param conceptTypeIdentifier the type name of concept
     * @param conceptIdentifier     the name of concept
     * @return record count is deleted
     */
    int deleteDynamicTaxonomySemantic(
        SPGTypeIdentifier conceptTypeIdentifier, ConceptIdentifier conceptIdentifier);

    /**
     * save or update dynamic taxonomy of concept.
     *
     * @param dynamicTaxonomySemantic semantic
     * @return record count is upsert
     */
    int upsertDynamicTaxonomySemantic(DynamicTaxonomySemantic dynamicTaxonomySemantic);

    /**
     * Query logical causation between concepts by condition.
     *
     * @param query query condition
     * @return list of concept semantic
     */
    List<LogicalCausationSemantic> queryLogicalCausationSemantic(LogicalCausationQuery query);

    /**
     * delete logical causation semantic.
     *
     * @param logicalCausationSemantic concept semantic model
     * @return record count
     */
    int deleteLogicalCausationSemantic(LogicalCausationSemantic logicalCausationSemantic);

    /**
     * Add logical causation semantic.
     *
     * @param logicalCausationSemantic concept semantic model
     * @return record count is created
     */
    int upsertLogicalCausationSemantic(LogicalCausationSemantic logicalCausationSemantic);
}
