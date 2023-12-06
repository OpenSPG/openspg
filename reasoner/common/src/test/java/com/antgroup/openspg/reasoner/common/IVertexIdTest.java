/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author donghai.ydh
 * @version IVertexIdTest.java, v 0.1 2023年08月07日 17:48 donghai.ydh
 */
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
