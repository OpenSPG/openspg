package com.antgroup.openspg.reasoner.common.graph.vertex.impl;

import lombok.Getter;

@Getter
public class VertexBizId extends VertexId {

  private String bizId;

  public VertexBizId(long internalId, String type) {
    super(internalId, type);
  }

  public VertexBizId(String bizId, String type) {
    super(bizId, type);
    this.bizId = bizId;
  }
}
