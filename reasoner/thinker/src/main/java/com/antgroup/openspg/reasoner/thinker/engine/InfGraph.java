package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.thinker.TripleStore;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.rule.ClauseEntry;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.visitor.RuleExecutor;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class InfGraph implements Graph {
  private LogicNetwork logicNetwork;
  private TripleStore tripleStore;
  private GraphStore graphStore;

  public InfGraph(LogicNetwork logicNetwork, GraphStore graphStore) {
    this.logicNetwork = logicNetwork;
    this.tripleStore = new MemTripleStore();
    this.graphStore = graphStore;
  }

  @Override
  public void init(Map<String, String> param) {
    this.graphStore.init(param);
  }

  @Override
  public List<Result> find(Triple pattern, Map<String, Object> context) {
    prepareContext(context);
    // Step1: find pattern in graph
    List<Result> dataInGraph = graphStore.find(pattern, context);
    if (CollectionUtils.isNotEmpty(dataInGraph)) {
      return dataInGraph;
    }
    return inference(pattern, context);
  }

  @Override
  public List<Result> find(Element s, Map<String, Object> context) {
    prepareContext(context);
    return inference(s, context);
  }

  private void prepareContext(Map<String, Object> context) {
    if (context != null) {
      for (Object val : context.values()) {
        if (val instanceof Entity) {
          addEntity((Entity) val);
        }
      }
    }
  }

  private List<Result> inference(Element pattern, Map<String, Object> context) {
    List<Result> rst = new LinkedList<>();
    for (Rule rule : logicNetwork.getBackwardRules(pattern)) {
      List<Element> body =
          rule.getBody().stream().map(ClauseEntry::toElement).collect(Collectors.toList());
      List<Result> data = prepareElements(body);
      if (CollectionUtils.isEmpty(data)) {
        continue;
      }
      TreeLogger traceLogger = new TreeLogger(rule.getRoot().toString());
      Boolean ret =
          rule.getRoot()
              .accept(
                  data.stream().map(Result::getData).collect(Collectors.toList()),
                  context,
                  new RuleExecutor(),
                  traceLogger);
      if (ret) {
        rst.add(new Result(rule.getHead().toElement(), traceLogger));
      }
    }
    return rst;
  }

  private List<Result> prepareElements(List<Element> body) {
    List<Result> elements = new ArrayList<>();
    for (Element pattern : body) {
      Collection<Result> spo = prepareElement(pattern);
      if (spo != null && !spo.isEmpty()) {
        elements.addAll(spo);
      }
    }
    return elements;
  }

  private Collection<Result> prepareElement(Element pattern) {
    Collection<Result> result;
    Collection<Element> spo = this.tripleStore.find(pattern);
    if (spo == null || spo.isEmpty()) {
      result = find(pattern, null);
    } else {
      result = spo.stream().map(e -> new Result(e, null)).collect(Collectors.toList());
    }
    return result;
  }

  public void addEntity(Entity entity) {
    this.tripleStore.addEntity(entity);
  }

  public void addTriple(Triple triple) {
    this.tripleStore.addTriple(triple);
  }

  public void clear() {
    this.tripleStore.clear();
  }
}
