package com.antgroup.openspg.cloudext.interfaces.graphstore.cmd;

import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGTypeNameConvertor;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class BatchVertexLPGRecordQuery extends BaseLPGRecordQuery {

  private final Set<String> vertexIds;
  private final String vertexName;

  public BatchVertexLPGRecordQuery(Set<String> vertexIds, String vertexName) {
    super(LpgRecordQueryType.BATCH_VERTEX);
    this.vertexIds = vertexIds;
    this.vertexName = vertexName;
  }

  @Override
  public String toScript(LPGTypeNameConvertor lpgTypeNameConvertor) {
    String convertedVertexName = lpgTypeNameConvertor.convertVertexTypeName(vertexName);
    return String.format(
        "Match (s:%s) WHERE s.id in [%s] RETURN s",
        convertedVertexName,
        vertexIds.stream().map(x -> String.format("'%s'", x)).collect(Collectors.joining(",")));
  }
}
