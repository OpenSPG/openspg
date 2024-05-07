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

package com.antgroup.openspg.reasoner.udf.test;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.reasoner.common.types.KTArray;
import com.antgroup.openspg.reasoner.common.types.KTBoolean$;
import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTList;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.UdfMng;
import com.antgroup.openspg.reasoner.udf.UdfMngFactory;
import com.antgroup.openspg.reasoner.udf.builtin.udf.Concat;
import com.antgroup.openspg.reasoner.udf.builtin.udf.ContainsAny;
import com.antgroup.openspg.reasoner.udf.model.IUdfMeta;
import com.antgroup.openspg.reasoner.udf.model.RuntimeUdfMeta;
import com.antgroup.openspg.reasoner.udf.model.UdfMeta;
import com.antgroup.openspg.reasoner.udf.model.UdfOperatorTypeEnum;
import com.antgroup.openspg.reasoner.udf.model.UdfParameterTypeHint;
import com.antgroup.openspg.reasoner.udf.utils.DateUtils;
import com.antgroup.openspg.reasoner.udf.utils.UdfUtils;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UdfTest {

  @Before
  public void init() {
    DateUtils.timeZone = TimeZone.getTimeZone("Asia/Shanghai");
  }

  @Test
  public void testReMatch() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta =
        mng.getUdfMeta("regex_match", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Object rst =
        udfMeta.invoke("Hello, my email address is example@example.com", "\\b\\w+@\\w+\\.\\w+\\b");
    Assert.assertEquals(rst, "example@example.com");
  }

  @Test
  public void testJsonGet() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    String params = "{'v':'123'}";
    IUdfMeta udfMeta =
        mng.getUdfMeta("json_get", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Object rst = udfMeta.invoke(params, "$.v");
    Assert.assertEquals(rst, "123");
  }

  @Test
  public void testJsonGet1() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    String params = "[{'v':'123'}]";
    IUdfMeta udfMeta =
        mng.getUdfMeta("json_get", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Object rst = udfMeta.invoke(params, "$.v");
    Assert.assertEquals(rst, "123");
  }

  @Test
  public void testJsonGet2() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    String params = "[{'v':'123'}, {'k':'456'}]";
    IUdfMeta udfMeta =
        mng.getUdfMeta("json_get", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Object rst = udfMeta.invoke(params, "$.k");
    Assert.assertEquals(rst, "456");
  }

  @Test
  public void testJsonGet3() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    String params = "[{'v': {'v1': '111', 'v2': '222'}}, {'k': {'k1': '333', 'k2': '444'}}]";
    IUdfMeta udfMeta =
        mng.getUdfMeta("json_get", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Object rst = udfMeta.invoke(params, "$.k.k2");
    Assert.assertEquals(rst, "444");
  }

  @Test
  public void testJsonGet4() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    String params = "[{'案由': '打架斗殴', '日期': '20240101'}, {'案由': '制造毒品', '日期': '20240202'}]";
    IUdfMeta udfMeta =
        mng.getUdfMeta("json_get", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Object rst = udfMeta.invoke(params, "$[案由 rlike '(.*)毒品(.*)'].案由");
    Assert.assertEquals(rst, "制造毒品");
  }

  @Test
  public void testRdfProperty() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    String params =
        "{\n"
            + "  \"B\": {\n"
            + "    \"extInfo\": \"gen_pic_gamble_hitsp_flag=1\",\n"
            + "    \"lbs\": \"{'v':'123'}\"\n"
            + "  }\n"
            + "}";
    ;
    Map<String, Object> paramsMap = JSONObject.parseObject(params, Map.class);
    paramsMap.put("basicInfo", "{'v':'123'}");
    IUdfMeta udfMeta =
        mng.getUdfMeta(
            "get_rdf_property", Lists.newArrayList(KTObject$.MODULE$, KTString$.MODULE$));
    Object rst = udfMeta.invoke(paramsMap, "v");
    Assert.assertEquals(rst, "123");
  }

  @Test
  public void testInStr() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta =
        mng.getUdfMeta(
            "in_str", Lists.newArrayList(KTString$.MODULE$, KTObject$.MODULE$, KTInteger$.MODULE$));
    Object rst = udfMeta.invoke("60岁", "岁", 1);
    Assert.assertEquals(rst, 3);

    IUdfMeta udfMeta2 =
        mng.getUdfMeta("in_str", Lists.newArrayList(KTString$.MODULE$, KTObject$.MODULE$));
    Object rst2 = udfMeta2.invoke("60岁", "岁");
    Assert.assertEquals(rst2, 3);
  }

  @Test
  public void testStrContains() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta =
        mng.getUdfMeta("str_contains", Lists.newArrayList(KTString$.MODULE$, KTObject$.MODULE$));
    Object rst = udfMeta.invoke("60岁", "岁");
    Assert.assertEquals(rst, true);
  }

  @Test
  public void testSubStr() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta =
        mng.getUdfMeta("sub_str", Lists.newArrayList(KTString$.MODULE$, KTInteger$.MODULE$));
    Object rst = udfMeta.invoke("60岁", 3);
    Assert.assertEquals(rst, "岁");

    IUdfMeta udfMeta2 =
        mng.getUdfMeta(
            "sub_str",
            Lists.newArrayList(KTString$.MODULE$, KTInteger$.MODULE$, KTInteger$.MODULE$));
    Object rst2 = udfMeta2.invoke("60岁", 3, 1);
    Assert.assertEquals(rst2, "岁");
  }

  @Test
  public void testCast() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta =
        mng.getUdfMeta("cast_type", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Object rst = udfMeta.invoke("60", "bigint");
    Assert.assertEquals(rst, 60L);
    rst = udfMeta.invoke("60.1", "float");
    Assert.assertEquals(rst, 60.1);
    try {
      udfMeta.invoke("abc", "float");
      Assert.assertTrue(false);
    } catch (Exception e) {
      Assert.assertTrue(true);
    }
  }

  @Test
  public void testConcat1() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta =
        mng.getUdfMeta("concat", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Object rst = udfMeta.invoke("str1", "str2");
    Assert.assertEquals(rst, "str1str2");
  }

  /** Support function overloading */
  @Test
  public void testConcat2() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta =
        mng.getUdfMeta("concat", Lists.newArrayList(KTLong$.MODULE$, KTString$.MODULE$));
    Object rst = udfMeta.invoke(1L, null);
    Assert.assertEquals(rst, "1null");
  }

  @Test
  public void testConcat3() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta =
        mng.getUdfMeta("concat", Lists.newArrayList(KTObject$.MODULE$, KTObject$.MODULE$));
    Object rst = udfMeta.invoke(1L, 's');
    Assert.assertEquals(rst, "1s");
  }

  @Test
  public void testRlike() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    RuntimeUdfMeta runtimeUdfMeta = mng.getRuntimeUdfMeta("rlike");
    Object rst1 =
        runtimeUdfMeta.invoke(
            "1345-123",
            "(63-)|(60-)|(66-)|(81-)|(84-)|(852-)|(855-)|(91-)|(95-)|(62-)|(853-)|(856-)|(886-)|(1345-)");
    Assert.assertEquals(rst1, true);
  }

  @Test
  public void testRuntimeUdfMeta() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    RuntimeUdfMeta runtimeUdfMeta = mng.getRuntimeUdfMeta("concat");
    Object rst1 = runtimeUdfMeta.invoke(1L, 2L, 3L);
    Assert.assertEquals(rst1, "123");

    Object rst2 = runtimeUdfMeta.invoke("str1", "str2");
    Assert.assertEquals(rst2, "str1str2");

    Object rst3 = runtimeUdfMeta.invoke(null, 0.5);
    Assert.assertEquals(rst3, "null0.5");
  }

  @Test
  public void testOperatorAndFunctionExcept() {
    Map<String, IUdfMeta> udfMetaMap = new HashMap<>();
    IUdfMeta udf1 =
        new UdfMeta(
            "t1",
            "t1",
            "",
            UdfOperatorTypeEnum.OPERATOR,
            new ArrayList<>(),
            KTLong$.MODULE$,
            null,
            null);
    IUdfMeta udf2 =
        new UdfMeta(
            "t1",
            "t1",
            "",
            UdfOperatorTypeEnum.FUNCTION,
            new ArrayList<>(),
            KTLong$.MODULE$,
            null,
            null);
    udfMetaMap.put("t1", udf1);
    udfMetaMap.put("t2", udf2);
    try {
      new RuntimeUdfMeta("t", udfMetaMap);
      Assert.fail();
    } catch (Exception e) {
      Assert.assertTrue(e.getMessage().contains("must has one udf type, FUNCTION or OPERATOR"));
    }
  }

  @Test
  public void testGetUdfList() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    List<IUdfMeta> udfMetaList = mng.getAllUdfMeta();
    Assert.assertTrue(udfMetaList.size() >= 3);

    Set<String> udfKeySet = new HashSet<>();
    udfMetaList.forEach(udfMeta -> udfKeySet.add(udfMeta.toString()));
    Assert.assertTrue(udfKeySet.contains("concat(KTString,KTString)->KTString"));
    Assert.assertTrue(udfKeySet.contains("concat(KTObject,KTObject)->KTString"));
    Assert.assertTrue(udfKeySet.contains("concat(KTObject,KTObject,KTObject)->KTString"));
  }

  @Test
  public void testGetRuntimeUdfList() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    List<RuntimeUdfMeta> udfMetaList = mng.getAllRuntimeUdfMeta();
    Assert.assertTrue(udfMetaList.size() >= 1);
    System.out.println(udfMetaList);
  }

  @Test
  public void testSum1() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta = mng.getUdfMeta("sum", Lists.newArrayList(new KTList(KTLong$.MODULE$)));
    List<Long> sumInputList = Lists.newArrayList(1L, 5L, 100L, 0L);
    Object rst = udfMeta.invoke(sumInputList);
    Assert.assertEquals(rst, 106L);
  }

  @Test
  public void testSum2() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    RuntimeUdfMeta runtimeUdfMeta = mng.getRuntimeUdfMeta("sum");
    List<Long> sumInputList = Lists.newArrayList(1L, 5L, 100L, 0L);
    Object rst = runtimeUdfMeta.invoke(sumInputList);
    Assert.assertEquals(rst, 106L);

    List<Double> sumInputDoubleList = Lists.newArrayList(0.5, 1.5, 2.5, 3.5);
    rst = runtimeUdfMeta.invoke(sumInputDoubleList);
    Assert.assertEquals(rst, 8.0);

    List<String> sumInputStringList = Lists.newArrayList("0.5", "1.5", "2.5", "3.5");
    rst = runtimeUdfMeta.invoke(sumInputDoubleList);
    Assert.assertEquals(rst, 8.0);
  }

  @Test
  public void testAvg1() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta = mng.getUdfMeta("avg", Lists.newArrayList(new KTList(KTDouble$.MODULE$)));
    List<Long> sumInputList = Lists.newArrayList(10L, 20L, 0L);
    Object rst = udfMeta.invoke(sumInputList);
    Assert.assertEquals(rst, 10.0);
  }

  @Test
  public void testCompatibleParamTypeList() {
    Iterator<List<KgType>> it =
        UdfUtils.getAllCompatibleParamTypeList(
            Lists.newArrayList(
                KTString$.MODULE$, KTString$.MODULE$, KTObject$.MODULE$, KTLong$.MODULE$));
    int count = 0;
    while (it.hasNext()) {
      List<KgType> kgTypeList = it.next();
      System.out.println(kgTypeList);
      count++;
    }
    Assert.assertEquals(count, 8);
  }

  @Test
  public void testMax() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta = mng.getUdfMeta("max", Lists.newArrayList(new KTList(KTDouble$.MODULE$)));
    List<Long> sumInputList = Lists.newArrayList(10L, 20L, 0L);
    Object rst = udfMeta.invoke(sumInputList);
    Assert.assertEquals(rst, 20L);
  }

  @Test
  public void testMax2() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta = mng.getUdfMeta("max", Lists.newArrayList(new KTList(KTString$.MODULE$)));
    List<String> sumInputList = Lists.newArrayList("00", "01", "02");
    Object rst = udfMeta.invoke(sumInputList);
    Assert.assertEquals(rst, "02");
  }

  @Test
  public void testMin() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta = mng.getUdfMeta("min", Lists.newArrayList(new KTList(KTString$.MODULE$)));
    List<String> sumInputList = Lists.newArrayList("00", "01", "02");
    Object rst = udfMeta.invoke(sumInputList);
    Assert.assertEquals(rst, "00");
  }

  @Test
  public void testRuleValue() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta =
        mng.getUdfMeta(
            "RULE_VALUE",
            Lists.newArrayList(KTBoolean$.MODULE$, KTObject$.MODULE$, KTObject$.MODULE$));
    Object rst = udfMeta.invoke(true, 1, 2);
    Assert.assertEquals(rst, 1);

    rst = udfMeta.invoke(false, 1, 2);
    Assert.assertEquals(rst, 2);
  }

  @Test
  public void testDayOfWeek() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta = mng.getUdfMeta("dayofweek", Lists.newArrayList(KTString$.MODULE$));
    Object rst = udfMeta.invoke("1681208495011");
    Assert.assertEquals(rst, 2);

    IUdfMeta udfMeta2 = mng.getUdfMeta("dayofweek", Lists.newArrayList(KTLong$.MODULE$));
    rst = udfMeta2.invoke(1681208495011L);
    Assert.assertEquals(rst, 2);
  }

  @Test
  public void testHourOfDay() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta udfMeta = mng.getUdfMeta("hourOFDAY", Lists.newArrayList(KTString$.MODULE$));
    Object rst = udfMeta.invoke("1681208495011");
    Assert.assertEquals(rst, 18);
  }

  @Test
  public void testGetUdfTypeHint() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    Map<String, Map<String, UdfParameterTypeHint>> allTypeHintMap = mng.getUdfTypeHint();
    Map<String, UdfParameterTypeHint> typeHints = allTypeHintMap.get("udf");
    Assert.assertTrue(typeHints.containsKey("concat"));
    Assert.assertEquals(
        typeHints.get("concat").getUdfParameterTypeMap().get(2).get(0)._1().get(0),
        KTObject$.MODULE$);
    Assert.assertEquals(
        typeHints.get("concat").getUdfParameterTypeMap().get(2).get(0)._1().get(1),
        KTObject$.MODULE$);
  }

  @Test
  public void testParamListCompare() {
    List<KgType> typeList1 = Lists.newArrayList(KTObject$.MODULE$, KTObject$.MODULE$);
    List<KgType> typeList2 = Lists.newArrayList(KTObject$.MODULE$, KTLong$.MODULE$);
    Assert.assertEquals(-1, UdfUtils.compareParamList(typeList1, typeList2));

    typeList1 = Lists.newArrayList(KTObject$.MODULE$, KTInteger$.MODULE$, KTObject$.MODULE$);
    typeList2 = Lists.newArrayList(KTObject$.MODULE$, KTLong$.MODULE$, KTObject$.MODULE$);
    Assert.assertEquals(0, UdfUtils.compareParamList(typeList1, typeList2));

    typeList1 = Lists.newArrayList(KTObject$.MODULE$, KTInteger$.MODULE$, KTObject$.MODULE$);
    typeList2 = Lists.newArrayList(KTObject$.MODULE$, KTObject$.MODULE$, KTObject$.MODULE$);
    Assert.assertEquals(1, UdfUtils.compareParamList(typeList1, typeList2));

    typeList1 = Lists.newArrayList(KTString$.MODULE$, KTObject$.MODULE$);
    typeList2 = Lists.newArrayList(KTObject$.MODULE$, KTObject$.MODULE$);
    Assert.assertEquals(1, UdfUtils.compareParamList(typeList1, typeList2));
  }

  @Test
  public void testParamListCompare2() {
    List<List<KgType>> typeListList =
        Lists.newArrayList(
            Lists.newArrayList(KTObject$.MODULE$, KTObject$.MODULE$, KTObject$.MODULE$),
            Lists.newArrayList(KTObject$.MODULE$, KTInteger$.MODULE$, KTObject$.MODULE$),
            Lists.newArrayList(KTLong$.MODULE$, KTInteger$.MODULE$, KTObject$.MODULE$),
            Lists.newArrayList(KTLong$.MODULE$, KTObject$.MODULE$, KTObject$.MODULE$),
            Lists.newArrayList(KTLong$.MODULE$, KTString$.MODULE$, KTDouble$.MODULE$));

    typeListList.sort((o1, o2) -> UdfUtils.compareParamList(o2, o1));

    List<KgType> firstTypeList = typeListList.get(0);
    Assert.assertEquals(firstTypeList.get(0), KTLong$.MODULE$);
    Assert.assertEquals(firstTypeList.get(1), KTString$.MODULE$);
    Assert.assertEquals(firstTypeList.get(2), KTDouble$.MODULE$);

    List<KgType> lastTypeList = typeListList.get(typeListList.size() - 1);
    Assert.assertEquals(lastTypeList.get(0), KTObject$.MODULE$);
    Assert.assertEquals(lastTypeList.get(1), KTObject$.MODULE$);
    Assert.assertEquals(lastTypeList.get(2), KTObject$.MODULE$);
  }

  @Test
  public void testArrayParameters() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta listConcat =
        mng.getUdfMeta("concat", Lists.newArrayList(new KTList(KTObject$.MODULE$)));
    List<Long> inputList = Lists.newArrayList(1L, 5L, 100L, 0L);
    Object rst = listConcat.invoke(inputList);
    Assert.assertEquals(rst, "151000");

    IUdfMeta arrayConcat =
        mng.getUdfMeta("concat", Lists.newArrayList(new KTArray(KTObject$.MODULE$)));
    Integer[] inputArray = new Integer[] {3, 54, 565, 650};
    rst = arrayConcat.invoke(new Object[] {inputArray});
    Assert.assertEquals(rst, "354565650");
  }

  @Test
  public void testContainsAny() {
    ContainsAny containsAny = new ContainsAny();
    String[] keywords = new String[3];
    keywords[0] = "est";
    keywords[1] = "aa";
    Assert.assertTrue(containsAny.containsAny("UTTest", keywords));

    keywords[0] = "test";
    Assert.assertFalse(containsAny.containsAny("UTTest", keywords));

    Assert.assertFalse(containsAny.containsAny(null, keywords));
    Assert.assertFalse(containsAny.containsAny("", keywords));
    Assert.assertFalse(containsAny.containsAny("UTTest", null));

    String[] emptyKeywords = new String[0];
    Assert.assertFalse(containsAny.containsAny("UTTest", emptyKeywords));

    Assert.assertFalse(containsAny.contains("UTTest", null));
    Assert.assertFalse(containsAny.contains("UTTest", "A"));
  }

  @Test
  public void testHash() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta hashUdf = mng.getUdfMeta("hash", Lists.newArrayList(KTObject$.MODULE$));
    Assert.assertEquals(hashUdf.invoke(1), 1);
  }

  @Test
  public void testConcatWs() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta concatWsUdf =
        mng.getUdfMeta(
            "concat_ws", Lists.newArrayList(KTString$.MODULE$, new KTList(KTObject$.MODULE$)));
    Assert.assertTrue(concatWsUdf.getCompatibleNames().contains("ConcatWs"));
    Object rst = concatWsUdf.invoke(",", Lists.newArrayList("1", "2", 3, null));
    Assert.assertEquals(rst, "1,2,3");

    IUdfMeta concatWsUdf2 =
        mng.getUdfMeta(
            "concat_ws", Lists.newArrayList(KTString$.MODULE$, new KTArray(KTObject$.MODULE$)));
    rst = concatWsUdf2.invoke(",", new Object[] {"a", "b", 'c', null});
    Assert.assertEquals(rst, "a,b,c");

    IUdfMeta concatWsUdf3 =
        mng.getUdfMeta(
            "concat_ws",
            Lists.newArrayList(KTString$.MODULE$, KTObject$.MODULE$, KTObject$.MODULE$));
    rst = concatWsUdf3.invoke(".", 4, 5);
    Assert.assertEquals(rst, "4.5");

    Concat concat = new Concat();
    Assert.assertEquals(concat.concatWs(",", 1, 2, 3), "1,2,3");
    Assert.assertEquals(concat.concatWs(",", 1, 2, 3, 4), "1,2,3,4");
    Assert.assertEquals(concat.concatWs(",", 1, 2, 3, 4, 5), "1,2,3,4,5");
    Assert.assertEquals(concat.concatWs(",", 1, 2, 3, 4, 5, 6), "1,2,3,4,5,6");
    Assert.assertEquals(concat.concatWs(",", 1, 2, 3, 4, 5, 6, 7), "1,2,3,4,5,6,7");
    Assert.assertEquals(concat.concatWs(",", 1, 2, 3, 4, 5, 6, 7, 8), "1,2,3,4,5,6,7,8");
    Assert.assertEquals(concat.concatWs(",", 1, 2, 3, 4, 5, 6, 7, 8, 9), "1,2,3,4,5,6,7,8,9");
    Assert.assertEquals(concat.concatWs(",", 1, 2, 3, 4, 5, 6, 7, 8, 9, null), "1,2,3,4,5,6,7,8,9");

    Assert.assertEquals(concat.concatWs(',', 1, 2, 3), "1,2,3");
    Assert.assertEquals(concat.concatWs(',', 1, 2, 3, 4), "1,2,3,4");
    Assert.assertEquals(concat.concatWs(',', 1, 2, 3, 4, 5), "1,2,3,4,5");
    Assert.assertEquals(concat.concatWs(',', 1, 2, 3, 4, 5, 6), "1,2,3,4,5,6");
    Assert.assertEquals(concat.concatWs(',', 1, 2, 3, 4, 5, 6, 7), "1,2,3,4,5,6,7");
    Assert.assertEquals(concat.concatWs(',', 1, 2, 3, 4, 5, 6, 7, 8), "1,2,3,4,5,6,7,8");
    Assert.assertEquals(concat.concatWs(',', 1, 2, 3, 4, 5, 6, 7, 8, 9), "1,2,3,4,5,6,7,8,9");
    Assert.assertEquals(
        concat.concatWs(',', 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), "1,2,3,4,5,6,7,8,9,10");
  }

  @Test
  public void testContainsTag() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta containsTagUdf =
        mng.getUdfMeta("contains_tag", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Assert.assertTrue(containsTagUdf.getCompatibleNames().contains("ContainsTag"));
    Object rst = containsTagUdf.invoke("1,2,3", "2,3");
    Assert.assertTrue((Boolean) rst);

    rst = containsTagUdf.invoke("", "");
    Assert.assertFalse((Boolean) rst);

    IUdfMeta containsTagUdf2 =
        mng.getUdfMeta(
            "contains_tag",
            Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$, KTString$.MODULE$));
    rst = containsTagUdf2.invoke("1_2_3", "3_5", "_");
    Assert.assertTrue((Boolean) rst);

    rst = containsTagUdf2.invoke(null, "", ",");
    Assert.assertFalse((Boolean) rst);

    rst = containsTagUdf2.invoke("1|2|3", "4|5", "|");
    Assert.assertFalse((Boolean) rst);
  }

  @Test
  public void testIsBlank() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta isBlankUdf = mng.getUdfMeta("is_blank", Lists.newArrayList(KTString$.MODULE$));
    Assert.assertTrue(isBlankUdf.getCompatibleNames().contains("IsBlank"));
    Assert.assertTrue((Boolean) isBlankUdf.invoke(""));
    Assert.assertTrue((Boolean) isBlankUdf.invoke(new Object[] {null}));

    IUdfMeta isNotBlankUdf = mng.getUdfMeta("is_not_blank", Lists.newArrayList(KTString$.MODULE$));
    Assert.assertTrue(isNotBlankUdf.getCompatibleNames().contains("IsNotBlank"));
    Assert.assertTrue((Boolean) isNotBlankUdf.invoke("1"));
  }

  @Test
  public void testStrLength() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta strLengthUdf = mng.getUdfMeta("str_length", Lists.newArrayList(KTString$.MODULE$));
    Assert.assertTrue(strLengthUdf.getCompatibleNames().contains("StrLength"));
    Assert.assertTrue(strLengthUdf.getCompatibleNames().contains("Length"));
    Assert.assertEquals(strLengthUdf.invoke(""), 0);
    Assert.assertEquals(strLengthUdf.invoke("a"), 1);
  }

  @Test
  public void testDateAdd() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta dateAddUdf =
        mng.getUdfMeta(
            "date_add",
            Lists.newArrayList(KTString$.MODULE$, KTInteger$.MODULE$, KTString$.MODULE$));
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("DateAdd"));
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("DtAdd"));
    Assert.assertEquals(dateAddUdf.invoke("20230711", 1, "dd"), "20230712");
    Assert.assertEquals(dateAddUdf.invoke("20230711", 1, "mm"), "20230811");
    Assert.assertEquals(dateAddUdf.invoke("20230711", 1, "yyyy"), "20240711");
    Assert.assertEquals(dateAddUdf.invoke("20230711 00:00:00", 1, "hh"), "20230711 01:00:00");
    Assert.assertEquals(dateAddUdf.invoke("20230711 00:00:00", 1, "ss"), "20230711 00:00:01");
    Assert.assertEquals(dateAddUdf.invoke("20230711", 1, "day"), "20230712");
    Assert.assertEquals(dateAddUdf.invoke("20230711", 1, "month"), "20230811");
    Assert.assertEquals(dateAddUdf.invoke("20230711", 1, "year"), "20240711");
    Assert.assertEquals(dateAddUdf.invoke("20230711 00:00:00", 1, "hour"), "20230711 01:00:00");
    Assert.assertEquals(dateAddUdf.invoke("20230711 00:00:00", 1, "minute"), "20230711 00:01:00");
    Assert.assertEquals(dateAddUdf.invoke("20230711 00:00:00", 1, "second"), "20230711 00:00:01");
    dateAddUdf =
        mng.getUdfMeta(
            "date_add",
            Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$, KTString$.MODULE$));
    Assert.assertEquals(dateAddUdf.invoke("20230711", "1", "dd"), "20230712");
  }

  @Test
  public void testDateDiff() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta dateAddUdf =
        mng.getUdfMeta("date_diff", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("DateDiff"));
    Assert.assertEquals(dateAddUdf.invoke("20230711 00:00:00", "20230730"), -19L);
  }

  @Test
  public void testDateFormat() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta dateAddUdf =
        mng.getUdfMeta("date_format", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("DateFormat"));
    Assert.assertEquals(dateAddUdf.invoke("20230711 00:00:00", "yyyyMMdd"), "20230711");

    dateAddUdf =
        mng.getUdfMeta(
            "date_format",
            Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$, KTString$.MODULE$));
    Assert.assertEquals(
        dateAddUdf.invoke("20230711 00:00:00", "yyyyMMdd HH:mm:ss", "yyyyMMdd"), "20230711");
  }

  @Test
  public void testFromUnixTime() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta dateAddUdf =
        mng.getUdfMeta("from_unix_time", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("FromUnixtime"));
    Assert.assertEquals(dateAddUdf.invoke("1688545587", "yyyyMMdd"), "20230705");

    dateAddUdf = mng.getUdfMeta("from_unix_time", Lists.newArrayList(KTString$.MODULE$));
    Assert.assertEquals(dateAddUdf.invoke("1688545587"), "2023-07-05 16:26:27");

    dateAddUdf = mng.getUdfMeta("from_unix_time", Lists.newArrayList(KTLong$.MODULE$));
    Assert.assertEquals(dateAddUdf.invoke(1688545587L), "2023-07-05 16:26:27");

    dateAddUdf =
        mng.getUdfMeta("from_unix_time", Lists.newArrayList(KTLong$.MODULE$, KTString$.MODULE$));
    Assert.assertEquals(dateAddUdf.invoke(1688545587L, "yyyyMMdd"), "20230705");
  }

  @Test
  public void testNow() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta dateAddUdf = mng.getUdfMeta("now", Lists.newArrayList());
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("Now"));
    Assert.assertEquals(dateAddUdf.invoke(), System.currentTimeMillis() / 1000);
  }

  @Test
  public void testTimeDiff() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta dateAddUdf =
        mng.getUdfMeta("time_diff", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("TimeDiff"));
    Assert.assertEquals(dateAddUdf.invoke("12345", "12344"), 1L);
    Assert.assertEquals(dateAddUdf.invoke("2023-07-05 16:26:27", "2023-07-05 16:26:26"), 1L);

    dateAddUdf = mng.getUdfMeta("time_diff", Lists.newArrayList(KTLong$.MODULE$, KTLong$.MODULE$));
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("TimeDiff"));
    Assert.assertEquals(dateAddUdf.invoke(12345L, 12344L), 1L);
  }

  @Test
  public void testToDate() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta dateAddUdf =
        mng.getUdfMeta("to_date", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("ToDate"));
    Assert.assertEquals(dateAddUdf.invoke("2023-07-05 16:26:27", "yyyyMMdd"), "20230705");

    dateAddUdf = mng.getUdfMeta("to_date", Lists.newArrayList(KTString$.MODULE$));
    Assert.assertEquals(dateAddUdf.invoke("2023-07-05 16:26:27"), "2023-07-05");

    Assert.assertEquals(dateAddUdf.invoke("abcdes"), null);
  }

  @Test
  public void testUnixTimeStamp() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta dateAddUdf =
        mng.getUdfMeta("unix_timestamp", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    Assert.assertTrue(dateAddUdf.getCompatibleNames().contains("UnixTimestamp"));
    Assert.assertEquals(
        dateAddUdf.invoke("2023-07-05 16:26:27", "yyyy-MM-dd HH:mm:ss"), 1688545587L);

    dateAddUdf = mng.getUdfMeta("unix_timestamp", Lists.newArrayList(KTString$.MODULE$));
    Assert.assertEquals(dateAddUdf.invoke("2023-07-05 16:26:27"), 1688545587L);
  }

  @Test
  public void testLower() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta lowerUdfMeta = mng.getUdfMeta("lower", Lists.newArrayList(KTObject$.MODULE$));
    Assert.assertTrue(lowerUdfMeta.getCompatibleNames().contains("Lower"));
    Assert.assertEquals(lowerUdfMeta.invoke("Low"), "low");
    Assert.assertEquals(lowerUdfMeta.invoke("low"), "low");
  }

  @Test
  public void testUpper() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta upperUdfMeta = mng.getUdfMeta("upper", Lists.newArrayList(KTObject$.MODULE$));
    Assert.assertTrue(upperUdfMeta.getCompatibleNames().contains("Upper"));
    Assert.assertEquals(upperUdfMeta.invoke("Upper"), "UPPER");
    Assert.assertEquals(upperUdfMeta.invoke("UPPER"), "UPPER");
  }

  @Test
  public void testTrim() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta trimUdfMeta = mng.getUdfMeta("trim", Lists.newArrayList(KTObject$.MODULE$));
    Assert.assertTrue(trimUdfMeta.getCompatibleNames().contains("Trim"));
    Assert.assertEquals(trimUdfMeta.invoke(""), "");
    Assert.assertEquals(trimUdfMeta.invoke(" abc "), "abc");
  }

  @Test
  public void testAbs() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta absUdfMeta = mng.getUdfMeta("abs", Lists.newArrayList(KTObject$.MODULE$));
    Assert.assertTrue(absUdfMeta.getCompatibleNames().contains("Abs"));
    Assert.assertEquals(absUdfMeta.invoke(new Object[] {null}), null);
    Assert.assertEquals(absUdfMeta.invoke("123"), null);
    Assert.assertEquals(absUdfMeta.invoke(-123), 123);
    Assert.assertEquals(absUdfMeta.invoke(123), 123);
    Assert.assertEquals(absUdfMeta.invoke(-123.0f), 123.0f);
    Assert.assertEquals(absUdfMeta.invoke(123f), 123f);
    Assert.assertEquals(absUdfMeta.invoke(-123.0), 123.0);
    Assert.assertEquals(absUdfMeta.invoke(123.0), 123.0);
    Assert.assertEquals(absUdfMeta.invoke(-123L), 123L);
    Assert.assertEquals(absUdfMeta.invoke(123L), 123L);
  }

  @Test
  public void testGeoDistance() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta geoDistanceUdfMeta =
        mng.getUdfMeta("geo_distance", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    String wkt1 =
        "POLYGON((111.70491 40.785621,111.705244 40.784815,111.705611 40.783997,111.705782 40.783617,111.705969 40.783243,111"
            + ".705957 40.783195,111.704255 40.782743,111.704047 40.783161,111.703855 40.783577,111.703528 40.784288,111"
            + ".702702 40"
            + ".785904,111.704559 40.786378,111.70491 40.785621))";
    String wkt2 =
        "POLYGON((111.705405 40.783523,111.70549 40.783329,111.705854 40.783417,111.70577 40.783613,111.705405 40.783523))";
    String distance = geoDistanceUdfMeta.invoke(wkt1, wkt2).toString();
    Assert.assertTrue(Double.parseDouble(distance) < 1e-9);

    Assert.assertTrue(geoDistanceUdfMeta.invoke("1", "2") == null);
  }

  @Test
  public void testGeoIntersectsArea() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta geoIntersectsAreaUdfMeta =
        mng.getUdfMeta(
            "geo_intersects_area", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    String wkt1 =
        "POLYGON((111.70491 40.785621,111.705244 40.784815,111.705611 40.783997,111.705782 40.783617,111.705969 40.783243,111"
            + ".705957 40.783195,111.704255 40.782743,111.704047 40.783161,111.703855 40.783577,111.703528 40.784288,111"
            + ".702702 40"
            + ".785904,111.704559 40.786378,111.70491 40.785621))";
    String wkt2 =
        "POLYGON((111.705405 40.783523,111.70549 40.783329,111.705854 40.783417,111.70577 40.783613,111.705405 40.783523))";
    String area = geoIntersectsAreaUdfMeta.invoke(wkt1, wkt2).toString();
    Assert.assertTrue(Double.parseDouble(area) > 736);
    Assert.assertTrue(geoIntersectsAreaUdfMeta.invoke("1", "2") == null);
  }

  @Test
  public void testGeoIntersectsShape() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta geoIntersectsShapeUdfMeta =
        mng.getUdfMeta(
            "geo_intersects_shape", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    String wkt1 =
        "POLYGON((111.70491 40.785621,111.705244 40.784815,111.705611 40.783997,111.705782 40.783617,111.705969 40.783243,111"
            + ".705957 40.783195,111.704255 40.782743,111.704047 40.783161,111.703855 40.783577,111.703528 40.784288,111.702702 40"
            + ".785904,111.704559 40.786378,111.70491 40.785621))";
    String wkt2 =
        "POLYGON((111.705405 40.783523,111.70549 40.783329,111.705854 40.783417,111.70577 40.783613,111.705405 40.783523))";
    String shape = geoIntersectsShapeUdfMeta.invoke(wkt1, wkt2).toString();
    Assert.assertTrue(
        shape.equals(
            "POLYGON ((111.705405 40.783523, 111.70577 40.783613, 111.705854 40.783417, "
                + "111.70549 40.783329, 111.705405 40.783523))"));
    Assert.assertTrue(geoIntersectsShapeUdfMeta.invoke("1", "2") == null);
  }

  @Test
  public void testGeoWithin() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta geoWithinUdfMeta =
        mng.getUdfMeta("geo_within", Lists.newArrayList(KTString$.MODULE$, KTString$.MODULE$));
    String wkt1 =
        "POLYGON((111.70491 40.785621,111.705244 40.784815,111.705611 40.783997,111.705782 40.783617,111.705969 40.783243,111"
            + ".705957 40.783195,111.704255 40.782743,111.704047 40.783161,111.703855 40.783577,111.703528 40.784288,111.702702 40"
            + ".785904,111.704559 40.786378,111.70491 40.785621))";
    String wkt2 =
        "POLYGON((111.705405 40.783523,111.70549 40.783329,111.705854 40.783417,111.70577 40.783613,111.705405 40.783523))";
    Object isWithin = geoWithinUdfMeta.invoke(wkt2, wkt1).toString();
    Assert.assertTrue(Boolean.parseBoolean(isWithin.toString()));
    Assert.assertTrue(geoWithinUdfMeta.invoke("1", "2") == null);
  }

  @Test
  public void testRandom() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta randomMeta = mng.getUdfMeta("random", Lists.newArrayList());
    Object value = randomMeta.invoke();
    Assert.assertTrue(value instanceof Integer);

    randomMeta = mng.getUdfMeta("random", Lists.newArrayList(KTInteger$.MODULE$));
    value = randomMeta.invoke(100);
    Assert.assertTrue((int) value < 100);

    randomMeta = mng.getUdfMeta("randomLong", Lists.newArrayList());
    value = randomMeta.invoke();
    Assert.assertTrue(value instanceof Long);
  }

  @Test
  public void testToTimeStamp() {
    UdfMng mng = UdfMngFactory.getUdfMng();
    IUdfMeta toTimestampUdf = mng.getUdfMeta("to_timestamp", Lists.newArrayList(KTLong$.MODULE$));
    Assert.assertTrue(toTimestampUdf.getCompatibleNames().contains("ToTimestamp"));
    System.out.println(toTimestampUdf.invoke(1688545587325L));
    Assert.assertEquals(toTimestampUdf.invoke(1688545587325L), "2023-07-05 16:26:27");
  }
}
