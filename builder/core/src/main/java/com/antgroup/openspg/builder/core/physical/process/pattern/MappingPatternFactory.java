package com.antgroup.openspg.builder.core.physical.process.pattern;

import com.antgroup.openspg.builder.model.exception.PipelineConfigException;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MappingPatternFactory {

  private static final Pattern GQL_REGEX =
      Pattern.compile(
          "^\\((?<startVertices>[\\w|.]+)\\)(-\\[(?<edges>[\\w|.]+)]->\\((?<endVertices>[\\w|.]+)\\))?$");

  private static final Pattern RELATION_REGEX =
      Pattern.compile("^(?<startVertices>[\\w|.]+)_(?<edges>\\w+)_(?<endVertices>[\\w|.]+)$");

  public static BaseSPGIdentifier identifierParse(String identifier) {
    if (StringUtils.isBlank(identifier)) {
      throw new PipelineConfigException("identifier for subgraph pattern cannot be blank.");
    }
    if (identifier.contains("_")) {
      return RelationIdentifier.parse(identifier);
    } else {
      return SPGTypeIdentifier.parse(identifier);
    }
  }

  public static BaseMappingPattern patternParse(String elements) {
    if (elements.contains(",")) {
      return getSubgraphMappingPattern(elements);
    } else {
      Matcher gqlMatcher = GQL_REGEX.matcher(elements);
      if (gqlMatcher.matches()) {
        String startVertices = gqlMatcher.group("startVertices");
        String edges = gqlMatcher.group("edges");
        String endVertices = gqlMatcher.group("endVertices");
        return new SubgraphMappingPattern().addIntoSubgraph(startVertices, edges, endVertices);
      } else {
        Matcher relationMatcher = RELATION_REGEX.matcher(elements);
        if (relationMatcher.matches()) {
          return new RelationMappingPattern(elements);
        } else {
          return new SPGTypeMappingPattern(elements);
        }
      }
    }
  }

  private static SubgraphMappingPattern getSubgraphMappingPattern(String elements) {
    SubgraphMappingPattern mappingPattern = new SubgraphMappingPattern();
    for (String element : elements.split(",")) {
      Matcher gqlMatcher = GQL_REGEX.matcher(element);
      if (gqlMatcher.matches()) {
        String startVertices = gqlMatcher.group("startVertices");
        String edges = gqlMatcher.group("edges");
        String endVertices = gqlMatcher.group("endVertices");
        mappingPattern.addIntoSubgraph(startVertices, edges, endVertices);
      } else {
        throw new PipelineConfigException("the part={} of elements is illegal", element);
      }
    }
    return mappingPattern;
  }
}
