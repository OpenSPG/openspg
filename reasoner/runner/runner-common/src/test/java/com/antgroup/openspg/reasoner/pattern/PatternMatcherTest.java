/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.pattern;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author peilong.zpl
 * @version $Id: PatternMatcherTest.java, v 0.1 2023-11-14 14:41 peilong.zpl Exp $$
 */
public class PatternMatcherTest {
    @Test
    public void testTimeOut() throws InterruptedException {
        PatternMatcher patternMatcher = new PatternMatcher("test", new MemGraphState());
        Thread.sleep(100);
        KgGraph<IVertexId> res = patternMatcher.patternMatch(IVertexId.from("test", "test"), null, null,
                null, null, null, null,
                null, null,
                null, null, false, 10);
        Assert.assertTrue(res == null);

    }
}