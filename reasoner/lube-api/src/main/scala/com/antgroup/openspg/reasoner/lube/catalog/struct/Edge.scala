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

package com.antgroup.openspg.reasoner.lube.catalog.struct

import com.antgroup.openspg.reasoner.common.concept.Predicate

/**
 * A edge defines relation exist in Knowledge Graph.
 *
 * @param startNode the start entity name of a relation.
 * @param typeName the type name of a relation.
 * @param endNode the end entity name of a relation.
 * @param properties the properties of a relation.
 * @param resolved has been resolved, it equals true if the property has been imported to KG
 *                 or has been computed.
 */
case class Edge(
    startNode: String,
    typeName: String,
    endNode: String,
    properties: Set[Field],
    resolved: Boolean)

/**
 * A edge defines [[Predicate]] relation between two different concept node.
 * @param parentConceptNode the parent concept node
 * @param predicate the concept node
 * @param conceptNode
 */
case class ConceptEdge(parentConceptNode: String, predicate: Predicate, conceptNode: String)
