package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import com.google.common.graph.Graph;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@SuppressWarnings({"UnstableApiUsage"})
public class SubgraphPattern extends BaseValObj {

  private final Graph<String> subgraph;

  /** 按照顺序返回子图元素列表，按照拓扑结构返回所有的点，再拓扑结构返回所有的边 */
  public List<String> elementOrdered() {
    return null;
  }
}
