package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.thinker.TripleStore;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Value;
import java.util.*;

public class MemTripleStore<K> implements TripleStore<K> {
  private Map<String, Entity<K>> entities;
  private Map<Element, List<Triple>> sToTriple;
  private Map<Element, List<Triple>> oToTriple;

  public MemTripleStore() {
    this.entities = new HashMap<>();
    this.sToTriple = new HashMap<>();
    this.oToTriple = new HashMap<>();
  }

  @Override
  public void init(Map<String, String> param) {}

  @Override
  public Collection<Element> find(Triple tripleMatch) {
    List<Element> elements = new LinkedList<>();
    if (tripleMatch.getSubject() instanceof Entity) {
      elements.addAll(sToTriple.getOrDefault(tripleMatch.getSubject(), new LinkedList<>()));
    } else if (tripleMatch.getObject() instanceof Entity) {
      elements.addAll(oToTriple.getOrDefault(tripleMatch.getObject(), new LinkedList<>()));
    } else if (tripleMatch.getSubject().equals(Element.ANY)
        && tripleMatch.getPredicate().equals(Element.ANY)
        && (tripleMatch.getObject() instanceof Entity)) {
      Entity<K> entity = entities.getOrDefault(getKey((Entity<K>) tripleMatch.getObject()), null);
      if (entity != null) {
        elements.add(entity);
      }
    } else {
      throw new RuntimeException("Cannot support " + tripleMatch);
    }
    return elements;
  }

  private String getKey(Entity<K> entity) {
    return entity.getId().toString() + entity.getType();
  }

  @Override
  public void addEntity(Entity<K> entity) {
    String key = getKey(entity);
    entities.put(key, entity);
  }

  @Override
  public void addTriple(Triple triple) {
    List<Triple> sTriples =
        sToTriple.computeIfAbsent(triple.getSubject(), (k) -> new LinkedList<>());
    sTriples.add(triple);
    if (!(triple.getObject() instanceof Value)) {
      List<Triple> oTriples =
          oToTriple.computeIfAbsent(triple.getSubject(), (k) -> new LinkedList<>());
      oTriples.add(triple);
    }
  }

  @Override
  public void clear() {
    this.entities.clear();
    this.sToTriple.clear();
    this.oToTriple.clear();
  }
}
