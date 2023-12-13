/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.loader.MemStartIdRecoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author donghai.ydh
 * @version MemStartIdRecoderTest.java, v 0.1 2023年05月30日 13:44 donghai.ydh
 */
public class MemStartIdRecoderTest {

    @Before
    public void init() {
    }

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