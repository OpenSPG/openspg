/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.reasoner.loader;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class MemStartIdRecoder extends BaseStartIdRecoder {
  // use TreeSet, sort vertex id
  protected Set<IVertexId> startIdSet = new TreeSet<>();

  protected Iterator<IVertexId> internalIt = null;

  @Override
  public void addStartId(IVertexId id) {
    startIdSet.add(id);
  }

  @Override
  public void flush() {
    this.internalIt = this.startIdSet.iterator();
  }

  @Override
  public long getStartIdCount() {
    return startIdSet.size();
  }

  @Override
  public boolean hasNext() {
    return this.internalIt.hasNext();
  }

  @Override
  public IVertexId next() {
    IVertexId id = this.internalIt.next();
    this.internalIt.remove();
    return id;
  }
}
