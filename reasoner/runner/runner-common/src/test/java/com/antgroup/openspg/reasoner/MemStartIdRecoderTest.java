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

package com.antgroup.openspg.reasoner;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.loader.MemStartIdRecoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MemStartIdRecoderTest {

  @Before
  public void init() {}

  @Test
  public void testMemStartId() {
    MemStartIdRecoder recoder = new MemStartIdRecoder();
    recoder.addStartId(IVertexId.from(1L, "t"));
    recoder.addStartId(IVertexId.from(2L, "t"));
    recoder.addStartId(IVertexId.from(2L, "t"));
    recoder.flush();

    Assert.assertEquals(2L, recoder.getStartIdCount());
    long count = 0;
    while (recoder.hasNext()) {
      IVertexId id = recoder.next();
      count++;
    }
    Assert.assertEquals(2L, count);
    Assert.assertEquals(0L, recoder.getStartIdCount());
  }
}
