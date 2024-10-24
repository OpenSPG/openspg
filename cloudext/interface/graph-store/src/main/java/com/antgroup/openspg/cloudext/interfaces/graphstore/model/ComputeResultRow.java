/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.cloudext.interfaces.graphstore.model;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComputeResultRow {

  private VertexRecord node;
  private double score;
}
