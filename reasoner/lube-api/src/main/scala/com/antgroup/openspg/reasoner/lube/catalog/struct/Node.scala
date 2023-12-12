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
