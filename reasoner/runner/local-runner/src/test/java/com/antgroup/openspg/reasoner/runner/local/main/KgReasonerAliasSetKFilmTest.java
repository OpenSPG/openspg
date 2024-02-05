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

package com.antgroup.openspg.reasoner.runner.local.main;

import com.antgroup.openspg.reasoner.runner.local.main.basetest.TransBaseTestData;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KgReasonerAliasSetKFilmTest {

  public interface TestFunction {
    void run();
  }

  public static class FileMutex {
    public static void runTestWithMutex(TestFunction function) {
      function.run();
    }
  }

  @Before
  public void init() {}

  @Test
  public void test1() {
    FileMutex.runTestWithMutex(this::doTest1);
  }

  private void doTest1() {
    String dsl =
        "\n"
            + "GraphStructure {\n"
            + "    (A:User)-[p1:trans]->(B:User)-[p2:trans]->(C:User)-[p3:trans]->(A)\n"
            + "}\n"
            + "Rule {\n"
            + "R1: A.id == $idSet1\n"
            + "R2: B.id in $idSet2\n"
            + "R3: C.id in $idSet2\n"
            + "totalTrans1 = group(A,B,C).sum(p1.amount)\n"
            + "totalTrans2 = group(A,B,C).sum(p2.amount)\n"
            + "totalTrans3 = group(A,B,C).sum(p3.amount)\n"
            + "totalTrans = totalTrans1 + totalTrans2 + totalTrans3\n"
            + "R2('取top2'): top(totalTrans, 2)"
            + "}\n"
            + "Action {\n"
            + "    get(A.id, B.id, C.id, totalTrans)\n"
            + "}";
    List<String[]> result =
        TransBaseTestData.runTestResult(
            dsl,
            new HashMap<String, Object>() {
              {
                put("idSet1", "'1'");
                put("idSet2", "['2', '3']");
              }
            });
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("1", result.get(0)[0]);
    Assert.assertEquals("2", result.get(0)[1]);
    Assert.assertEquals("3", result.get(0)[2]);
  }

  @Test
  public void test2() {
    FileMutex.runTestWithMutex(this::doTest2);
  }

  private void doTest2() {
    String dsl =
        "\n"
            + "GraphStructure {\n"
            + "    (A:User)-[p1:trans]->(B:User)-[p2:trans]->(C:User)-[p3:trans]->(A)\n"
            + "}\n"
            + "Rule {\n"
            + "R1: A.id in $idSet1\n"
            + "R2: B.id in $idSet2\n"
            + "R3: C.id in $idSet2\n"
            + "totalTrans = p1.amount + p2.amount + p3.amount\n"
            + "R2('取top2'): top(totalTrans, 3)"
            + "}\n"
            + "Action {\n"
            + "    get(A.id, B.id, C.id, totalTrans)\n"
            + "}";
    List<String[]> result =
        TransBaseTestData.runTestResult(
            dsl,
            new HashMap<String, Object>() {
              {
                put("idSet1", "['1', '4', '5']");
                put("idSet2", "['2', '3']");
              }
            });
    Assert.assertEquals(3, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("500", result.get(0)[3]);
    Assert.assertEquals("500", result.get(1)[3]);
    Assert.assertEquals("500", result.get(2)[3]);
  }

  @Test
  public void test3() {
    FileMutex.runTestWithMutex(this::doTest3);
  }

  private void doTest3() {
    String dsl =
        "\n"
            + "GraphStructure {\n"
            + "    (A:User)-[p1:trans]->(B:User)-[p2:trans]->(C:User)-[p3:trans]->(A)\n"
            + "}\n"
            + "Rule {\n"
            + "R1: A.id == $idSet1\n"
            + "R2: B.id == $idSet2\n"
            + "R3: C.id == $idSet3\n"
            + "totalTrans = p1.amount + p2.amount + p3.amount\n"
            + "R2('取top2'): top(totalTrans, 3)"
            + "}\n"
            + "Action {\n"
            + "    get(A.id, B.id, C.id, totalTrans)\n"
            + "}";
    List<String[]> result =
        TransBaseTestData.runTestResult(
            dsl,
            new HashMap<String, Object>() {
              {
                put("idSet1", "'1'");
                put("idSet2", "'2'");
                put("idSet3", "'3'");
              }
            });
    Assert.assertEquals(3, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("350", result.get(0)[3]);
    Assert.assertEquals("350", result.get(1)[3]);
    Assert.assertEquals("350", result.get(2)[3]);
  }

  @Test
  public void test4() {
    FileMutex.runTestWithMutex(this::doTest4);
  }

  private void doTest4() {
    String dsl =
        "\n"
            + "GraphStructure {\n"
            + "  A [User, __start__='true']\n"
            + "  B,C [User]\n"
            + "  A->B [trans] as p1\n"
            + "  B->C [trans] as p2\n"
            + "  C->A [trans] as p3"
            + "}\n"
            + "Rule {\n"
            + "R1: A.id in $idSet1\n"
            + "R2: B.id in $idSet2\n"
            + "R3: C.id in $idSet2\n"
            + "t1 = group(A,B,C).sum(p1.amount)\n"
            + "t2 = group(A,B,C).sum(p2.amount)\n"
            + "t3 = group(A,B,C).sum(p3.amount)\n"
            + "totalSum =  t1 + t2 + t3"
            + "}\n"
            + "Action {\n"
            + "    get(A.id, B.id, C.id, totalSum)\n"
            + "}";
    List<String[]> result =
        TransBaseTestData.runTestResult(
            dsl,
            new HashMap<String, Object>() {
              {
                put("idSet1", "['1']");
                put("idSet2", "['2', '3']");
              }
            });
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("1", result.get(0)[0]);
    Assert.assertEquals("2", result.get(0)[1]);
    Assert.assertEquals("3", result.get(0)[2]);
    Assert.assertEquals("700.0", result.get(0)[3]);
  }
}
