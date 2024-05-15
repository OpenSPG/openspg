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

package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Node;
import java.util.List;
import java.util.Map;

public interface Thinker {
  void init(Map<String, String> params);

  List<Result> find(Element s, Element p, Element o);

  List<Result> find(Element s, Element p, Element o, Map<String, Object> context);

  List<Result> find(Node target, Map<String, Object> context);
}
