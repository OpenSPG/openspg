/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.common.util.neo4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RelationLabelConstraint {

  private final String startNodeLabel;
  private final String edgeLabel;
  private final String endNodeLabel;
}
