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

package com.antgroup.openspg.reasoner.udf.test;

import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.udf.UdfMng;
import com.antgroup.openspg.reasoner.udf.UdfMngFactory;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdafMeta;
import com.antgroup.openspg.reasoner.udf.utils.DateUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UdafTest {
  @Before
  public void init() {
    DateUtils.timeZone = TimeZone.getTimeZone("Asia/Shanghai");
  }

  @Test
  public void testAggCount1() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    UdafMeta udafMeta = mng.getUdafMeta("Count", KTObject$.MODULE$);

    // suppose on worker 1
    BaseUdaf aggregateFunctionOnWorker1 = udafMeta.createAggregateFunction();
    aggregateFunctionOnWorker1.initialize();
    aggregateFunctionOnWorker1.update("one row");

    // suppose on worker 2
    BaseUdaf aggregateFunctionOnWorker2 = udafMeta.createAggregateFunction();
    aggregateFunctionOnWorker2.initialize();
    aggregateFunctionOnWorker2.update("one row");
    aggregateFunctionOnWorker2.update("one row");

    // suppose on reduce worker
    aggregateFunctionOnWorker1.merge(aggregateFunctionOnWorker2);

    // final result
    Assert.assertEquals(aggregateFunctionOnWorker1.evaluate(), 3L);
  }

  @Test
  public void testAggCount2() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    UdafMeta udafMeta = mng.getUdafMeta("Count", KTLong$.MODULE$);

    // suppose on worker 1
    BaseUdaf aggregateFunctionOnWorker1 = udafMeta.createAggregateFunction();
    aggregateFunctionOnWorker1.initialize();
    aggregateFunctionOnWorker1.update(1L);

    // suppose on worker 2
    BaseUdaf aggregateFunctionOnWorker2 = udafMeta.createAggregateFunction();
    aggregateFunctionOnWorker2.initialize();
    aggregateFunctionOnWorker2.update(2L);

    // suppose on reduce worker
    aggregateFunctionOnWorker1.merge(aggregateFunctionOnWorker2);

    // final result
    Assert.assertEquals(aggregateFunctionOnWorker1.evaluate(), 2L);
  }

  @Test
  public void testGetUdafMetaList() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    List<UdafMeta> udafMetaList = mng.getAllUdafMeta();
    Assert.assertTrue(udafMetaList.size() >= 1);

    Set<String> udfKeySet = new HashSet<>();
    udafMetaList.forEach(udfMeta -> udfKeySet.add(udfMeta.toString()));
    Assert.assertTrue(udfKeySet.contains("count(KTObject)->KTLong"));
  }

  @Test
  public void testAggSum() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    UdafMeta udafMeta = mng.getUdafMeta("Sum", KTLong$.MODULE$);

    BaseUdaf aggregateFunctionOnWorker1 = udafMeta.createAggregateFunction();
    aggregateFunctionOnWorker1.initialize();
    aggregateFunctionOnWorker1.update(1L);
    aggregateFunctionOnWorker1.update(10L);

    BaseUdaf aggregateFunctionOnWorker2 = udafMeta.createAggregateFunction();
    aggregateFunctionOnWorker2.initialize();
    aggregateFunctionOnWorker2.update(100L);

    aggregateFunctionOnWorker1.merge(aggregateFunctionOnWorker2);

    Assert.assertEquals(111L, aggregateFunctionOnWorker1.evaluate());
  }

  @Test
  public void testAggCountDistinct() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    UdafMeta udafMeta = mng.getUdafMeta("count_distinct", KTObject$.MODULE$);
    BaseUdaf aggregateFunctionOnWorker1 = udafMeta.createAggregateFunction();
    aggregateFunctionOnWorker1.initialize();
    aggregateFunctionOnWorker1.update(1);
    aggregateFunctionOnWorker1.update(2);
    aggregateFunctionOnWorker1.update(2);
    BaseUdaf aggregateFunctionOnWorker2 = udafMeta.createAggregateFunction();
    aggregateFunctionOnWorker2.initialize();
    aggregateFunctionOnWorker2.update(2);
    aggregateFunctionOnWorker2.update(3);
    aggregateFunctionOnWorker1.merge(aggregateFunctionOnWorker2);
    Assert.assertEquals(3L, aggregateFunctionOnWorker1.evaluate());
  }
}
