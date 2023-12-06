/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.graph.vertex.impl;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;

/**
 * @author chengqiang.cq
 * @version $Id: Vertex.java, v 0.1 2023-02-01 11:30 chengqiang.cq Exp $$
 */
public class MirrorVertex<K, VV> extends NoneVertex<K, VV> {

  /** mirror vertex for optional */
  public MirrorVertex(IVertex<K, VV> vertex) {
    super(vertex);
  }

  @Override
  public MirrorVertex<K, VV> clone() {
    return new MirrorVertex<>(this);
  }
}
