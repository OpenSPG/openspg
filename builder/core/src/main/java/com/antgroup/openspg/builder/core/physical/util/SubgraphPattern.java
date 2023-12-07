package com.antgroup.openspg.builder.core.physical.util;

import com.antgroup.openspg.builder.model.exception.PipelineConfigException;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.traverse.TopologicalOrderIterator;

@AllArgsConstructor
public class SubgraphPattern extends BaseValObj {

  @Getter
  @AllArgsConstructor
  private static class LabeledEdge extends DefaultEdge {
    private final String label;

    @Override
    public String toString() {
      return String.format("%s_%s_%s", getSource(), label, getTarget());
    }
  }

  private static final Pattern GQL_REGEX =
      Pattern.compile(
          "^\\((?<startVertices>[\\w|.]+)\\)(-\\[(?<edges>[\\w|.]+)]->\\((?<endVertices>[\\w|.]+)\\))?$");

  private static final Pattern RELATION_REGEX =
      Pattern.compile("^(?<startVertices>[\\w|.]+)_(?<edges>\\w+)_(?<endVertices>[\\w|.]+)$");

  @Getter private final boolean singleVertex;
  @Getter private final boolean singleEdge;
  private final Graph<BaseSPGIdentifier, LabeledEdge> subgraph;

  /** 按照顺序返回子图元素列表，按照逆拓扑结构返回所有的点，再逆拓扑结构返回所有的边 */
  public List<BaseSPGIdentifier> elementOrdered() {
    List<BaseSPGIdentifier> topoVertices = new ArrayList<>();

    try {
      TopologicalOrderIterator<BaseSPGIdentifier, LabeledEdge> iterator =
          new TopologicalOrderIterator<>(subgraph);
      while (iterator.hasNext()) {
        BaseSPGIdentifier spgType = iterator.next();
        topoVertices.add(spgType);
      }

      Collections.reverse(topoVertices);
      if (singleEdge) {
        topoVertices.clear();
      }
      for (LabeledEdge edge : subgraph.edgeSet()) {
        topoVertices.add(edge.toString());
      }
    } catch (IllegalArgumentException e) {
      throw new PipelineConfigException(e.getMessage(), e.getCause());
    }
    return topoVertices;
  }

  public static SubgraphPattern from(String elements) {
    Graph<BaseSPGIdentifier, LabeledEdge> subgraph =
        GraphTypeBuilder.<BaseSPGIdentifier, DefaultEdge>directed()
            .allowingSelfLoops(false)
            .allowingMultipleEdges(true)
            .edgeClass(LabeledEdge.class)
            .weighted(false)
            .buildGraph();

    boolean singleVertex = false;
    boolean singleEdge = false;
    for (String element : elements.split(",")) {
      Matcher gqlMatcher = GQL_REGEX.matcher(element);
      if (gqlMatcher.matches()) {
        String startVertices = gqlMatcher.group("startVertices");
        String edges = gqlMatcher.group("edges");
        String endVertices = gqlMatcher.group("endVertices");
        addIntoSubgraph(subgraph, startVertices, edges, endVertices);
      } else {
        Matcher relationMatcher = RELATION_REGEX.matcher(element);
        if (relationMatcher.matches()) {
          singleEdge = true;
          String startVertices = relationMatcher.group("startVertices");
          String edges = relationMatcher.group("edges");
          String endVertices = relationMatcher.group("endVertices");
          addIntoSubgraph(subgraph, startVertices, edges, endVertices);
        } else {
          singleVertex = true;
          addIntoSubgraph(subgraph, element, null, null);
        }
      }
    }
    return new SubgraphPattern(singleVertex, singleEdge, subgraph);
  }

  private static void addIntoSubgraph(
      Graph<BaseSPGIdentifier, LabeledEdge> subgraph,
      String startVertices,
      String edges,
      String endVertices) {
    if (StringUtils.isBlank(startVertices)) {
      throw new PipelineConfigException();
    }

    String[] ss = startVertices.split("\\|");
    for (String s : ss) {
      subgraph.addVertex(SPGTypeIdentifier.parse(s));
    }
    if (edges != null) {
      if (StringUtils.isBlank(edges) || StringUtils.isBlank(endVertices)) {
        throw new PipelineConfigException();
      }

      String[] ps = edges.split("\\|");
      String[] os = endVertices.split("\\|");
      for (String o : os) {
        subgraph.addVertex(SPGTypeIdentifier.parse(o));
      }
      for (String s : ss) {
        for (String p : ps) {
          for (String o : os) {
            subgraph.addEdge(
                SPGTypeIdentifier.parse(s), SPGTypeIdentifier.parse(o), new LabeledEdge(p));
          }
        }
      }
    }
  }

  @Override
  public String toString() {
    return subgraph.toString();
  }
}
