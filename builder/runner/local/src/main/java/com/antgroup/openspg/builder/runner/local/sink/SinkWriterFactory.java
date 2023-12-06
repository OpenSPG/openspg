package com.antgroup.openspg.builder.runner.local.sink;

import com.antgroup.openspg.builder.core.logical.BaseLogicalNode;
import com.antgroup.openspg.builder.model.pipeline.config.BaseNodeConfig;
import com.antgroup.openspg.builder.runner.local.sink.impl.GraphStoreSinkWriter;

public class SinkWriterFactory {

  public static <T extends BaseNodeConfig> BaseSinkWriter<T> getSinkWriter(BaseLogicalNode<T> baseNode) {
    switch (baseNode.getType()) {
      case GRAPH_SINK:
        return new GraphStoreSinkWriter(
            baseNode.getId(), baseNode.getName(), baseNode.getNodeConfig());
      default:
        throw new IllegalArgumentException("illegal nodeType=" + baseNode.getType());
    }
  }
}
