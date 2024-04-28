package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.thinker.TripleStore;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.rule.ClauseEntry;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class InfGraph<K> implements Graph<K> {
  private LogicNetwork logicNetwork;
  private TripleStore<K> tripleStore;
  private GraphStore<K> graphStore;

  public InfGraph(LogicNetwork logicNetwork, GraphStore<K> graphStore) {
    this.logicNetwork = logicNetwork;
    this.tripleStore = new MemTripleStore<K>();
    this.graphStore = graphStore;
  }

  @Override
  public void init(Map<String, String> param) {
    this.graphStore.init(param);
  }

  @Override
  public List<Element> find(Triple pattern, TreeLogger treeLogger, Map<String, Object> context) {
    // Step1: find pattern in graph
    List<Element> dataInGraph = graphStore.find(pattern, treeLogger, context);
    if (CollectionUtils.isNotEmpty(dataInGraph)) {
      return dataInGraph;
    }

    if (context != null) {
      for (Object val : context.values()) {
        if (val instanceof Entity) {
          addEntity((Entity<K>) val);
        }
      }
    }

    for (Rule rule : logicNetwork.getBackwardRules(pattern)) {
      List<Triple> body =
          rule.getBody().stream()
              .map(ClauseEntry::toElement)
              .map(e -> toTripleMatch(e))
              .collect(Collectors.toList());
      List<Element> data = prepareElements(body, treeLogger);
    }
    return null;
  }

  private List<Element> prepareElements(List<Triple> body, TreeLogger treeLogger) {
    List<Element> elements = new ArrayList<>();
    for (Triple pattern : body) {
      Collection<Element> spo = prepareElement(pattern, treeLogger);
      if (spo == null || spo.isEmpty()) {
        return null;
      } else {
        elements.addAll(spo);
      }
    }
    return elements;
  }

  private Collection<Element> prepareElement(Triple pattern, TreeLogger logger) {
    Collection<Element> spo = this.tripleStore.find(pattern);
    if (spo == null || spo.isEmpty()) {
      spo = find(pattern, logger, null);
    }
    return spo;
  }

  private Triple toTripleMatch(Element element) {
    if (element instanceof Entity) {
      return Triple.create(null, null, element);
    } else {
      return (Triple) element;
    }
  }

  public void addEntity(Entity<K> entity) {
    this.tripleStore.addEntity(entity);
  }

  public void addTriple(Triple triple) {
    this.tripleStore.addTriple(triple);
  }

  public void clear() {
    this.tripleStore.clear();
  }
}
