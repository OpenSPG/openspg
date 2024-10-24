/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.cloudext.interfaces.graphstore.cmd;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import java.util.List;
import lombok.Data;

@Data
public class PageRankCompete {

  private List<VertexRecord> startVertices;
  private String targetVertexType;
  private Float dampingFactor = 0.85f;
  private Integer maxIterations = 20;

  public PageRankCompete(List<VertexRecord> startVertices, String targetVertexType) {
    this.startVertices = startVertices;
    this.targetVertexType = targetVertexType;
  }
}
