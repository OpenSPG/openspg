package com.antgroup.openspg.builder.runner.local.source;

import com.antgroup.openspg.builder.core.logical.BaseLogicalNode;
import com.antgroup.openspg.builder.model.pipeline.config.BaseNodeConfig;
import com.antgroup.openspg.builder.runner.local.source.impl.CsvFileSourceReader;

public class SourceReaderFactory {

  public static <T extends BaseNodeConfig> BaseSourceReader<T> getSourceReader(
      BaseLogicalNode<T> baseNode) {
    switch (baseNode.getType()) {
      case CSV_SOURCE:
        return new CsvFileSourceReader(
            baseNode.getId(), baseNode.getName(), baseNode.getNodeConfig());
      default:
        throw new IllegalArgumentException("illegal nodeType=" + baseNode.getType());
    }
  }
}
