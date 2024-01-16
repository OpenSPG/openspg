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

package com.antgroup.openspg.reasoner.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import org.junit.Assert;
import org.junit.Test;

public class IVertexIdTest {

  @Test
  public void testFromBytes() {
    byte[] idBytes = new byte[16];
    IVertexId id = IVertexId.from(idBytes);
    Assert.assertEquals(id.getBytes(), idBytes);
  }

  @Test
  public void testFromBytesLenError() {
    try {
      IVertexId.from(new byte[] {});
    } catch (Exception e) {
      Assert.assertTrue(true);
      return;
    }
    Assert.assertTrue(false);
  }
}
