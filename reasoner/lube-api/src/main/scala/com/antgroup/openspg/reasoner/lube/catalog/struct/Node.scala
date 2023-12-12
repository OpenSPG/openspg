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

package com.antgroup.openspg.reasoner.lube.catalog.struct

/**
 * A node defines an entity exist in Knowledge Graph.
 *
 * @param typeName the name of an entity
 * @param nodeType the type of an entity
 * @param properties the properties of an entity
 * @param resolved has been resolved, it equals true if the property has been imported to KG
 *                 or has been computed.
 */
case class Node(
    typeName: String,
    nodeType: NodeType.Value,
    properties: Set[Field],
    resolved: Boolean)
