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
package com.antgroup.openspg.reasoner.pattern;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import org.junit.Assert;
import org.junit.Test;

public class PatternMatcherTest {
  @Test
  public void testTimeOut() throws InterruptedException {
    PatternMatcher patternMatcher = new PatternMatcher("test", new MemGraphState());
    Thread.sleep(100);
    KgGraph<IVertexId> res =
        patternMatcher.patternMatch(
            IVertexId.from("test", "test"),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            10);
    Assert.assertTrue(res == null);
  }
}
