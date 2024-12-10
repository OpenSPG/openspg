/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.cloudext.interfaces.graphstore.cmd;

import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGTypeNameConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.Direction;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.PropertyFilter;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import java.util.List;
import lombok.*;
import org.apache.commons.lang3.NotImplementedException;

@Getter
public class PathMatchQuery extends BaseLPGRecordQuery {

  private final String srcVertexName;

  private final List<String> srcVertexIds;

  public PathMatchQuery(List<String> srcVertexIds, String srcVertexName) {
    super(LpgRecordQueryType.PATH_MATCH);
    this.srcVertexIds = srcVertexIds;
    this.srcVertexName = srcVertexName;
  }

  @Setter private VertexMatchRule srcVertexRule;

  @Setter private List<HopMatchRule> hops;

  @Setter private PageRule pageRule;

  @Setter private SortRule sortRule;

  @Data
  @NoArgsConstructor
  public static class HopMatchRule {

    private EdgeMatchRule edgeRule;

    private VertexMatchRule adjacentVertexRule;
  }

  @Data
  public abstract static class BaseMatchRule {

    private List<PropertyFilter> propFilters;

    private List<String> returnProps;
  }

  @NoArgsConstructor
  public static class VertexMatchRule extends BaseMatchRule {}

  @Setter
  @Getter
  @NoArgsConstructor
  public static class EdgeMatchRule extends BaseMatchRule {

    private List<EdgeTypeName> edgeTypeNameConstraint;

    private Direction direction = Direction.BOTH;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PageRule {

    private Integer offset = 0;

    private Integer limit = 50;

    public PageRule(Integer limit) {
      this.limit = limit;
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SortRule {

    private Boolean sortByVertexProperty;

    private Integer hopIndex;

    private String propertyName;

    private SortStrategy strategy;

    public enum SortStrategy {
      DESC,
      ASC
    }
  }

  @Override
  public String toScript(LPGTypeNameConvertor lpgTypeNameConvertor) {
    throw new NotImplementedException("PathMatchQuery.toScript() is not supported yet.");
  }
}
