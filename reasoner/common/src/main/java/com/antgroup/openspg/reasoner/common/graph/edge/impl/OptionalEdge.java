/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.graph.edge.impl;

/**
 * @author chengqiang.cq
 * @version $Id: Edge.java, v 0.1 2023-02-01 11:37 chengqiang.cq Exp $$
 */
public class OptionalEdge<K, EV> extends Edge<K, EV> {

  /** optional edge */
  public OptionalEdge(K srcId, K targetId) {
    super(srcId, targetId);
  }

  @Override
  public OptionalEdge<K, EV> clone() {
    return new OptionalEdge<>(this.sourceId, this.targetId);
  }
}
