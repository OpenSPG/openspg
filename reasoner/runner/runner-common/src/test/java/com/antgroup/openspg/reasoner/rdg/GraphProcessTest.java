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
package com.antgroup.openspg.reasoner.rdg;

import com.antgroup.openspg.reasoner.lube.common.expr.AggOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.AggUdf$;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.expr.Ref$;
import com.antgroup.openspg.reasoner.lube.common.expr.VString$;
import com.antgroup.openspg.reasoner.rdg.common.groupProcess.AggOpProcessBaseGroupProcess;
import com.google.common.collect.Sets;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.JavaConversions;

public class GraphProcessTest {
  @Test
  public void testGetUnDefineUdaf() {
    Set<Expr> args = Sets.newHashSet(VString$.MODULE$.apply("test"));
    Aggregator aggregator =
        new AggOpExpr(
            AggUdf$.MODULE$.apply("unDefineUdf", JavaConversions.asScalaSet(args).toList()),
            Ref$.MODULE$.apply("test"));
    try {
      AggOpProcessBaseGroupProcess groupProcess =
          new AggOpProcessBaseGroupProcess("", null, aggregator);
      Assert.fail();
    } catch (Exception e) {
      Assert.assertTrue(
          e.getMessage().contains("unsupported aggregator function, type=unDefineUdf"));
    }
  }
}
