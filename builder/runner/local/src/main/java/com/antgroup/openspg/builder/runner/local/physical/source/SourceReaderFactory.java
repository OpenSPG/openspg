package com.antgroup.openspg.builder.runner.local.physical.source;

import com.antgroup.openspg.builder.core.logical.BaseLogicalNode;
import com.antgroup.openspg.builder.core.logical.CsvSourceNode;
import com.antgroup.openspg.builder.runner.local.physical.source.impl.CsvFileSourceReader;

public class SourceReaderFactory {

  public static BaseSourceReader<?> getSourceReader(BaseLogicalNode<?> baseNode) {
    switch (baseNode.getType()) {
      case CSV_SOURCE:
        return new CsvFileSourceReader(
            baseNode.getId(), baseNode.getName(), ((CsvSourceNode) baseNode).getNodeConfig());
      default:
        throw new IllegalArgumentException("illegal nodeType=" + baseNode.getType());
    }
  }
}
