/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner;

import com.antgroup.openspg.reasoner.batching.DynamicBatchSize;
import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author donghai.ydh
 * @version RunnerUtilTest.java, v 0.1 2023年05月22日 16:22 donghai.ydh
 */
public class RunnerUtilTest {

    @Test
    public void testBetween() {
        Assert.assertEquals(RunnerUtil.between(null, null, 100L), true);
        Assert.assertEquals(RunnerUtil.between(10L, null, 10L), true);
        Assert.assertEquals(RunnerUtil.between(10L, null, 1L), false);
        Assert.assertEquals(RunnerUtil.between(null, 10L, 100L), false);
        Assert.assertEquals(RunnerUtil.between(null, 100L, 100L), true);
        Assert.assertEquals(RunnerUtil.between(1L, 10L, 100L), false);
        Assert.assertEquals(RunnerUtil.between(1L, 10L, 10L), true);
    }

    @Test
    public void testEdgeContext() {
        IEdge<IVertexId, IProperty> edge = new Edge<>(IVertexId.from(0, "s"), IVertexId.from(1, "o"), new EdgeProperty(),
                0L, Direction.OUT, "s_p_o");
        Map<String, Object> edgeContext = RunnerUtil.edgeContext(edge, null, "E");
        Assert.assertEquals(((Map<String, Object>) edgeContext.get("E")).get(Constants.CONTEXT_LABEL), "p");
    }

    @Test
    public void testDynamicBatch() {
        final long allSize = 51L;
        final long minSize = 5;
        final long maxSize = 20L;
        DynamicBatchSize batchSize = new DynamicBatchSize(allSize, minSize, maxSize, 4);
        Assert.assertEquals(batchSize.remainSize(), allSize);
        long size1 = batchSize.getNextBatchSize();
        System.out.println("size1=" + size1);
        Utils.sleep(35);
        long size2 = batchSize.getNextBatchSize();
        System.out.println("size2=" + size2);
        Utils.sleep(100);
        long size3 = batchSize.getNextBatchSize();
        System.out.println("size3=" + size3);
        long size4 = batchSize.getNextBatchSize();
        System.out.println("size4=" + size4);
    }

    @Test
    public void testGetDslParams() {
        Map<String, Object> dslParams = RunnerUtil.getOfflineDslParams(new HashMap<>(), false);
        Assert.assertNotNull(dslParams);
    }

    @Test
    public void testStarPathLimit1() {
        Map<String, List<IEdge<IVertexId, IProperty>>> adjEdges = new HashMap<>();
        adjEdges.put("E1", Lists.newArrayList(
                new Edge<>(IVertexId.from(11L, "A"), IVertexId.from(21L, "B"))
                , new Edge<>(IVertexId.from(12L, "A"), IVertexId.from(22L, "B"))
                , new Edge<>(IVertexId.from(13L, "A"), IVertexId.from(23L, "B"))
                , new Edge<>(IVertexId.from(14L, "A"), IVertexId.from(24L, "B"))
                , new Edge<>(IVertexId.from(15L, "A"), IVertexId.from(25L, "B"))
                , new Edge<>(IVertexId.from(16L, "A"), IVertexId.from(26L, "B"))
                , new Edge<>(IVertexId.from(17L, "A"), IVertexId.from(27L, "B"))
                , new Edge<>(IVertexId.from(18L, "A"), IVertexId.from(28L, "B"))
                , new Edge<>(IVertexId.from(19L, "A"), IVertexId.from(29L, "B"))
        ));
        adjEdges.put("E2", Lists.newArrayList(
                new Edge<>(IVertexId.from(11L, "A"), IVertexId.from(21L, "B"))
                , new Edge<>(IVertexId.from(12L, "A"), IVertexId.from(22L, "B"))
                , new Edge<>(IVertexId.from(13L, "A"), IVertexId.from(23L, "B"))
                , new Edge<>(IVertexId.from(14L, "A"), IVertexId.from(24L, "B"))
        ));
        // minEdgeNum too big, will not limit edge size
        RunnerUtil.doStarPathLimit(adjEdges, 1, 100);
        Assert.assertEquals(adjEdges.get("E1").size(), 9);
        Assert.assertEquals(adjEdges.get("E2").size(), 4);

        // edge not exceed limit
        RunnerUtil.doStarPathLimit(adjEdges, 36, 3);
        Assert.assertEquals(adjEdges.get("E1").size(), 9);
        Assert.assertEquals(adjEdges.get("E2").size(), 4);

        // less than shrinkFactorThreshold, will not limit edge size
        RunnerUtil.doStarPathLimit(adjEdges, 34, 3);
        Assert.assertEquals(adjEdges.get("E1").size(), 9);
        Assert.assertEquals(adjEdges.get("E2").size(), 4);

        // will do edge limit action
        RunnerUtil.doStarPathLimit(adjEdges, 20, 3);
        Assert.assertEquals(adjEdges.get("E1").size(), 7);
        Assert.assertEquals(adjEdges.get("E2").size(), 3);

        // do nothing
        RunnerUtil.doStarPathLimit(adjEdges, 20, 2);
        Assert.assertEquals(adjEdges.get("E1").size(), 7);
        Assert.assertEquals(adjEdges.get("E2").size(), 3);
    }

    @Test
    public void testCompare() {
        Assert.assertEquals(0, RunnerUtil.compareTwoObject(null, null));
        Assert.assertEquals(-1, RunnerUtil.compareTwoObject(null, 1));
        Assert.assertEquals(1, RunnerUtil.compareTwoObject(1, null));
        Object obj1 = 0L;
        Object obj2 = 0L;
        Assert.assertEquals(0, RunnerUtil.compareTwoObject(obj1, obj2));
        obj1 = 1L;
        Assert.assertEquals(1, RunnerUtil.compareTwoObject(obj1, obj2));
        obj1 = 0L;
        obj2 = 1L;
        Assert.assertEquals(-1, RunnerUtil.compareTwoObject(obj1, obj2));
    }
}