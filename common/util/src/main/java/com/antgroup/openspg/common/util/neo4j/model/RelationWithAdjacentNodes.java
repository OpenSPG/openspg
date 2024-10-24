/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.common.util.neo4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

@Getter
@AllArgsConstructor
public class RelationWithAdjacentNodes {

  private final Node startNode;
  private final Relationship relationship;
  private final Node endNode;
}
