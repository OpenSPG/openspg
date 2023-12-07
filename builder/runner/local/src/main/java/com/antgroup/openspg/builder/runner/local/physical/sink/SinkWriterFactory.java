package com.antgroup.openspg.builder.runner.local.physical.sink;

import com.antgroup.openspg.builder.core.logical.BaseLogicalNode;
import com.antgroup.openspg.builder.core.logical.GraphStoreSinkNode;
import com.antgroup.openspg.builder.runner.local.physical.sink.impl.GraphStoreSinkWriter;

public class SinkWriterFactory {

  public static BaseSinkWriter<?> getSinkWriter(BaseLogicalNode<?> baseNode) {
    switch (baseNode.getType()) {
      case GRAPH_SINK:
        return new GraphStoreSinkWriter(
            baseNode.getId(), baseNode.getName(), ((GraphStoreSinkNode) baseNode).getNodeConfig());
      default:
        throw new IllegalArgumentException("illegal nodeType=" + baseNode.getType());
    }
  }
}
