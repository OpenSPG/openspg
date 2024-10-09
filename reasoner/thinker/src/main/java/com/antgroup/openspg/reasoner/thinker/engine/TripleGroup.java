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

import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.*;

public class TripleGroup {
  private List<Triple> triples;

  public TripleGroup(List<Triple> triples) {
    this.triples = triples;
  }

  public List<List<Triple>> group() {
    Map<String, String> resp = new HashMap<>();
    for (Triple triple : triples) {
      String s = findRootRep(triple.getSubject().alias(), resp);
      String o = findRootRep(triple.getObject().alias(), resp);
      if (s.compareTo(o) > 0) {
        resp.put(o, s);
        resp.put(triple.alias(), s);
      } else {
        resp.put(s, o);
        resp.put(triple.alias(), o);
      }
    }
    Set<String> cluster = new HashSet<>();
    for (String key : resp.keySet()) {
      cluster.add(findRootRep(key, resp));
    }
    List<List<Triple>> groups = new ArrayList<>(cluster.size());
    for (String g : cluster) {
      List<Triple> group = new ArrayList<>();
      for (Triple triple : triples) {
        String c = findRootRep(triple.getSubject().alias(), resp);
        if (g.equals(c)) {
          group.add(triple);
        }
      }
      groups.add(group);
    }
    return groups;
  }

  private String findRootRep(String cur, Map<String, String> rep) {
    if (!rep.containsKey(cur)) {
      rep.put(cur, cur);
      return cur;
    }
    if (rep.get(cur).equals(cur)) {
      return cur;
    }

    String repStr = findRootRep(rep.get(cur), rep);
    rep.put(cur, repStr);
    return repStr;
  }
}
