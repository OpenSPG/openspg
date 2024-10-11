/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.thinker.TripleStore;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Node;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Predicate;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.rule.ClauseEntry;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.visitor.RuleExecutor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InfGraph implements Graph {
  private static final Logger logger = LoggerFactory.getLogger(InfGraph.class);

  private LogicNetwork logicNetwork;
  private TripleStore tripleStore;
  private GraphStore graphStore;
  private Set<Triple> recorder;

  public InfGraph(LogicNetwork logicNetwork, GraphStore graphStore) {
    this.logicNetwork = logicNetwork;
    this.tripleStore = new MemTripleStore();
    this.graphStore = graphStore;
    this.recorder = new HashSet<>();
  }

  @Override
  public void init(Map<String, String> param) {
    this.graphStore.init(param);
  }

  @Override
  public List<Result> find(Triple pattern, Map<String, Object> context) {
    logger.info("InfGraph find pattern={}, context={}", pattern, context);
    List<Result> result = new LinkedList<>();

    // Step1: find pattern in context or tripleStore
    Collection<Element> spo = this.tripleStore.find(pattern);
    if (CollectionUtils.isNotEmpty(spo)) {
      result.addAll(
          spo.stream().map(e -> new Result(e.bind(pattern), null)).collect(Collectors.toList()));
    }
    // Step2: find pattern in graph
    List<Result> dataInGraph = graphStore.find(pattern, context);
    logger.info("GraphStore find pattern={}, result={}", pattern, dataInGraph);
    if (CollectionUtils.isNotEmpty(dataInGraph)) {
      for (Result tri : dataInGraph) {
        addTriple((Triple) tri.getData());
        result.add(tri);
      }
    }
    // TODO 像是记录推理的哪些Pattern，应该写在最前呀
    recorder.add((Triple) pattern.cleanAlias());
    // Step3: inference pattern
    List<Result> infResult = inference(pattern, context);
    logger.info("InfGraph infer pattern={}, result={}", pattern, infResult);
    result.addAll(infResult);
    return result;
  }

  @Override
  public void prepare(Map<String, Object> context) {
    if (context != null) {
      for (Object val : context.values()) {
        if (val instanceof Entity) {
          addEntity((Entity) val);
        } else if (val instanceof Triple) {
          addTriple((Triple) val);
        }
      }
    }
  }

  private List<Result> inference(Triple pattern, Map<String, Object> context) {
    List<Result> rst = new LinkedList<>();
    boolean strictMode =
        (Boolean) context.getOrDefault(Constants.SPG_REASONER_THINKER_STRICT, false);
    for (Rule rule : logicNetwork.getBackwardRules(pattern)) {
      List<Triple> body =
          rule.getBody().stream().map(ClauseEntry::toTriple).collect(Collectors.toList());
      List<List<Result>> data = prepareElements(body, rule.getHead().toTriple(), pattern, context);
      if (CollectionUtils.isEmpty(data)) {
        TreeLogger traceLogger = new TreeLogger(rule.getRoot().toString());
        Boolean ret =
            rule.getRoot()
                .accept(new LinkedList<>(), context, new RuleExecutor(strictMode), traceLogger);
        traceLogger.setCurrentNodeMsg(rule.getDesc());
        if (ret) {
          Element ele = rule.getHead().toTriple();
          rst.add(new Result(ele, traceLogger));
          if (ele instanceof Triple) {
            addTriple((Triple) ele);
          } else {
            addEntity((Entity) ele);
          }
        }
      } else {
        for (List<Result> d : data) {
          TreeLogger traceLogger = new TreeLogger(rule.getRoot().toString());
          List<Element> dList = d.stream().map(Result::getData).collect(Collectors.toList());
          Boolean ret =
              rule.getRoot().accept(dList, context, new RuleExecutor(strictMode), traceLogger);
          List<String> msg =
              d.stream()
                  .map(Result::getTraceLog)
                  .filter(l -> l != null)
                  .map(TreeLogger::getCurrentNodeMsg)
                  .filter(m -> StringUtils.isNotBlank(m))
                  .collect(Collectors.toList());
          List<String> msgs = new LinkedList<>(msg);
          if (StringUtils.isNotBlank(rule.getDesc())) {
            msgs.add(rule.getDesc());
          }
          traceLogger.setCurrentNodeMsg(StringUtils.join(msgs, ";"));
          if (ret) {
            Element ele = rule.getHead().toTriple();
            rst.add(new Result(bindResult(dList, ele), traceLogger));
            if (ele instanceof Triple) {
              addTriple((Triple) ele);
            } else {
              addEntity((Entity) ele);
            }
          }
        }
      }
    }
    return rst;
  }

  private Element bindResult(List<Element> data, Element pattern) {
    Map<String, Element> map = new HashMap<>();
    for (Element e : data) {
      if (e instanceof Entity || e instanceof Node) {
        map.put(e.alias(), e);
      } else if (e instanceof Triple) {
        map.put(((Triple) e).getSubject().alias(), ((Triple) e).getSubject());
        map.put(((Triple) e).getObject().alias(), ((Triple) e).getObject());
      }
    }
    if (pattern instanceof Entity) {
      return pattern;
    } else if (pattern instanceof Node) {
      return map.get(pattern.alias());
    }
    Triple t = (Triple) pattern;
    return new Triple(
        map.getOrDefault(t.getSubject().alias(), ((Triple) pattern).getSubject()),
        t.getPredicate(),
        map.getOrDefault(t.getObject().alias(), ((Triple) pattern).getObject()));
  }

  private Triple binding(Triple triple, Triple pattern) {
    Map<String, Element> aliasToElement = new HashMap<>();
    aliasToElement.put(pattern.getSubject().alias(), pattern.getSubject());
    aliasToElement.put(pattern.getObject().alias(), pattern.getObject());
    Element sub = aliasToElement.getOrDefault(triple.getSubject().alias(), triple.getSubject());
    Element pre = aliasToElement.getOrDefault(triple.getPredicate().alias(), triple.getPredicate());
    Element obj = aliasToElement.getOrDefault(triple.getObject().alias(), triple.getObject());
    return new Triple(sub, pre, obj);
  }

  // TODO 别名的解决是，比如规则1中用到的规则2，但别名与规则2不同，那在计算规则2时用head的别名代替Pattern？
  private List<List<Result>> prepareElements(
      List<Triple> body, Triple head, Triple pattern, Map<String, Object> context) {
    List<List<Result>> elements = new ArrayList<>();
    // TODO 为什么不直接用head作为起点，而是要将其bind到Pattern上？
    // 1. 别名更换，2. node的实例化
    Triple bindingPattern = (Triple) pattern.bind(head);
    List<Triple> bindingBody = new ArrayList<>(body.size());
    for (Triple e : body) {
      bindingBody.add(binding(e, bindingPattern));
    }
    TripleGroup tripleGroup = new TripleGroup(bindingBody);
    List<List<Triple>> groups = tripleGroup.group();
    for (List<Triple> group : groups) {
      // TODO starts是什么作用
      Map<String, Element> starts = getStart(group);
      Set<Triple> choose = new HashSet<>();
      while (choose.size() < group.size()) {
        for (Triple e : group) {
          if (choose.contains(e)) {
            continue;
          }
          Collection<String> curStart =
              CollectionUtils.intersection(starts.keySet(), tripleAlias(e));
          if (curStart.isEmpty()) {
            continue;
          } else if (e.getSubject() instanceof Predicate) {
            if (choose.stream()
                    .filter(ele -> ele instanceof Triple)
                    .filter(ele -> ele.getPredicate().alias() == e.getSubject().alias())
                    .count()
                == 0) {
              continue;
            }
          }
          choose.add(e);
          Element s = starts.get(curStart.iterator().next());
          if (s.alias().equals(e.getSubject().alias())) {
            starts.put(e.getObject().alias(), e.getObject());
          } else {
            starts.put(e.getSubject().alias(), e.getSubject());
          }
          starts.put(e.alias(), e);
          if (CollectionUtils.isEmpty(elements)) {
            Triple triple = bindTriple(null, s, e);
            if (triple != null) {
              List<List<Result>> singeRst = prepareElement(null, triple, context);
              if (CollectionUtils.isNotEmpty(singeRst)) {
                elements.addAll(singeRst);
              }
            }
          } else {
            List<List<Result>> tmpElements = new LinkedList<>();
            for (List<Result> evidence : elements) {
              Triple triple = bindTriple(evidence, s, e);
              if (triple != null) {
                List<List<Result>> singeRst = prepareElement(evidence, triple, context);
                if (CollectionUtils.isNotEmpty(singeRst)) {
                  tmpElements.addAll(singeRst);
                }
              }
            }
            elements.clear();
            elements = tmpElements;
          }
        }
      }
    }
    return elements;
  }

  private Set<String> tripleAlias(Triple triple) {
    return new HashSet<>(Arrays.asList(triple.getSubject().alias(), triple.getObject().alias()));
  }

  private Triple bindTriple(List<Result> evidence, Element s, Triple triple) {
    Map<String, Element> aliasToElement = new HashMap<>();
    if (CollectionUtils.isNotEmpty(evidence)) {
      for (Result r : evidence) {
        Element data = r.getData();
        aliasToElement.put(data.alias(), data);
        if (data instanceof Triple) {
          aliasToElement.put(((Triple) data).getSubject().alias(), ((Triple) data).getSubject());
          aliasToElement.put(((Triple) data).getObject().alias(), ((Triple) data).getObject());
        }
      }
    }
    if (!aliasToElement.containsKey(s.alias())) {
      aliasToElement.put(s.alias(), s);
    }
    Element sub = aliasToElement.getOrDefault(triple.getSubject().alias(), triple.getSubject());
    Element pre = aliasToElement.getOrDefault(triple.getPredicate().alias(), triple.getPredicate());
    Element obj = aliasToElement.getOrDefault(triple.getObject().alias(), triple.getObject());
    return new Triple(sub, pre, obj);
  }

  private Map<String, Element> getStart(List<Triple> triples) {
    Map<String, Element> starts = new HashMap<>();
    for (Triple triple : triples) {
      if (triple.getSubject() instanceof Entity) {
        starts.put(triple.getSubject().alias(), triple.getSubject());
        break;
      } else if (triple.getObject() instanceof Entity) {
        starts.put(triple.getObject().alias(), triple.getObject());
        break;
      }
    }
    if (!starts.isEmpty()) {
      return starts;
    }
    for (Triple triple : triples) {
      if (triple.getSubject() instanceof Node) {
        starts.put(triple.getSubject().alias(), triple.getSubject());
        break;
      } else if (triple.getObject() instanceof Node) {
        starts.put(triple.getObject().alias(), triple.getObject());
        break;
      }
    }
    return starts;
  }

  private List<List<Result>> prepareElement(
      List<Result> evidences, Triple pattern, Map<String, Object> context) {
    List<List<Result>> rst = new LinkedList<>();
    if (CollectionUtils.isEmpty(evidences)) {
      Collection<Result> curRst = prepareElement(pattern, context);
      for (Result r : curRst) {
        rst.add(new LinkedList<>(Arrays.asList(r)));
      }
      return rst;
    }
    Collection<Result> curRst = prepareElement(pattern, context);
    for (Result r : curRst) {
      List<Result> merged = new LinkedList<>(evidences);
      merged.add(r);
      // TODO 这个保证应该在语法阶段就确认了
      if (reserve(merged)) {
        rst.add(merged);
      }
    }
    return rst;
  }

  private Boolean reserve(List<Result> evidence) {
    Map<String, String> aliasToId = new HashMap<>();
    for (Result r : evidence) {
      Element e = r.getData();
      if (e instanceof Entity) {
        if (!aliasToId.containsKey(e.alias())) {
          aliasToId.put(e.alias(), ((Entity) e).getId());
        }
        if (!StringUtils.equals(((Entity) e).getId(), aliasToId.get(e.alias()))) {
          return false;
        }
        // TODO 为什么会有Triple的subject又是Triple的情况
        //p.disclaimType
      } else if (!(((Triple) e).getSubject() instanceof Triple)) {
        if (((Triple) e).getSubject() instanceof Entity) {
          Entity s = (Entity) ((Triple) e).getSubject();
          if (!aliasToId.containsKey(s.alias())) {
            aliasToId.put(s.alias(), s.getId());
          }
          if (!StringUtils.equals(s.getId(), aliasToId.get(s.alias()))) {
            return false;
          }
        }
        if (((Triple) e).getObject() instanceof Entity) {
          Entity o = (Entity) ((Triple) e).getObject();

          if (!aliasToId.containsKey(o.alias())) {
            aliasToId.put(o.alias(), o.getId());
          }

          if (!StringUtils.equals(o.getId(), aliasToId.get(o.alias()))) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * rule1 -> rule2 -> rule1
   *
   * @param pattern
   * @param context
   * @return
   */
  private Collection<Result> prepareElement(Element pattern, Map<String, Object> context) {
    Collection<Result> result = new LinkedList<>();
    Triple triple = Triple.create(pattern);
    Collection<Element> spo = this.tripleStore.find(triple);
    if (spo == null || spo.isEmpty()) {
      // TODO 这里是为了防止对同一个Triple递归吗
      if (!recorder.contains(triple.cleanAlias())) {
        result = find(triple, context);
      }
    } else {
      result = spo.stream().map(e -> new Result(e, null)).collect(Collectors.toList());
    }
    for (Result r : result) {
      r.setData(r.getData().bind(triple));
    }
    return result;
  }

  private void addEntity(Entity entity) {
    Triple triple = new Triple(Element.ANY, Predicate.CONCLUDE, entity);
    this.addTriple(triple);
  }

  private void addTriple(Triple triple) {
    this.tripleStore.addTriple(triple);
  }

  public void clear() {
    this.tripleStore.clear();
    this.recorder.clear();
  }
}
