/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg;


import java.util.Set;

import com.antgroup.openspg.reasoner.lube.common.expr.AggOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.AggUdf$;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.expr.Ref$;
import com.antgroup.openspg.reasoner.lube.common.expr.VString$;
import com.antgroup.openspg.reasoner.rdg.common.groupProcess.AggOpProcessBaseGroupProcess;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.JavaConversions;


public class GraphProcessTest {
    @Test
    public void testGetUnDefineUdaf() {
        Set<Expr> args = Sets.newHashSet(VString$.MODULE$.apply("test"));
        Aggregator aggregator = new AggOpExpr(AggUdf$.MODULE$.apply("unDefineUdf", JavaConversions.asScalaSet(args).toList()),
                Ref$.MODULE$.apply("test"));
        try {
            AggOpProcessBaseGroupProcess groupProcess = new AggOpProcessBaseGroupProcess(
                    "",
                    null,
                    aggregator
            );
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("unsupported aggregator function, type=unDefineUdf"));
        }


    }
}