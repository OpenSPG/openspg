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

import com.antgroup.openspg.reasoner.runner.local.main.basetest.FilmBaseTestData;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KgReasonerTopKFilmTest {

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

  private List<String[]> runTestResult(String dsl) {
    return FilmBaseTestData.runTestResult(
        dsl,
        "com.antgroup.openspg.reasoner.runner.local.main.basetest.FilmBaseTestData$FilmGraphGeneratorTopK");
  }

  @Test
  public void test1() {
    FileMutex.runTestWithMutex(this::doTest1);
  }

  private void doTest1() {
    String dsl =
        "Define (s:Film)-[p:olderStar]->(o:FilmStar) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[:starOfFilm]->(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R1(\"只取年纪最大的演员\"): group(s).desc(o.age).limit(1)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s)-[:olderStar]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("L1_1_star", result.get(0)[1]);
  }

  @Test
  public void test2() {
    FileMutex.runTestWithMutex(this::doTest2);
  }

  private void doTest2() {
    String dsl =
        "Define (s:Film)-[p:youngStar]->(o:FilmStar) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[:starOfFilm]->(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R1(\"只取年纪最小的演员\"): group(s).asc(o.age).limit(1)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s)-[:youngStar]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("L1_3_star", result.get(0)[1]);
  }

  @Test
  public void test3() {
    FileMutex.runTestWithMutex(this::doTest3);
  }

  private void doTest3() {
    String dsl =
        "Define (s:Film)-[p:firstJoinStar]->(o:FilmStar) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[sf:starOfFilm]->(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R1(\"只取最早加入的演员\"): group(s).asc(sf.joinTs).limit(1)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s)-[:firstJoinStar]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("L1_1_star", result.get(0)[1]);
  }

  @Test
  public void test4() {
    FileMutex.runTestWithMutex(this::doTest4);
  }

  private void doTest4() {
    String dsl =
        "Define (s:Film)-[p:lastJoinStar]->(o:FilmStar) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[sf:starOfFilm]->(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R1(\"只取最晚加入的演员\"): group(s).desc(sf.joinTs).limit(1)\n"
            + "        p.joinTs = sf.joinTs \n"
            + "    }\n"
            + "}\n"
            + "GraphStructure {\n"
            + "    (s)-[p:lastJoinStar]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id, p.joinTs)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("L1_3_star", result.get(0)[1]);
    Assert.assertEquals("400", result.get(0)[2]);
  }

  @Test
  public void test5() {
    FileMutex.runTestWithMutex(this::doTest5);
  }

  private void doTest5() {
    String dsl =
        "Define (s:Film)-[p:mostSameStarFilm]->(o:Film) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[sf:starOfFilm]->(star:FilmStar)<-[sf2:starOfFilm]-(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        sameStarNum(\"得到相同演员数目\") = group(s,o).count(star)\n"
            + "        R1(\"值取最大值\"): group(s).desc(sameStarNum).limit(1)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s)-[:mostSameStarFilm]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("f1", result.get(0)[1]);
  }

  @Test
  public void test6() {
    FileMutex.runTestWithMutex(this::doTest6);
  }

  private void doTest6() {
    String dsl =
        "Define (s:Film)-[p:lessSameStarFilm]->(o:Film) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[sf:starOfFilm]->(star:FilmStar)<-[sf2:starOfFilm]-(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        sameStarNum(\"得到相同演员数目\") = group(s,o).count(star)\n"
            + "        R1(\"值取最大值\"): group(s).asc(sameStarNum).limit(1)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s)-[:lessSameStarFilm]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("f3", result.get(0)[1]);
  }

  @Test
  public void test7() {
    FileMutex.runTestWithMutex(this::doTest7);
  }

  private void doTest7() {
    String dsl =
        "Define (s:Film)-[p:mostSameStarFilm]->(o:Film) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[sf:starOfFilm]->(star:FilmStar)<-[sf2:starOfFilm]-(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        sameStarNum(\"得到相同男演员数目\") = group(s,o).countIf(star.gender == '男', star)\n"
            + "        R1(\"值取最大值\"): group(s).desc(sameStarNum).limit(1)\n"
            + "    }\n"
            + "}\n"
            + "GraphStructure {\n"
            + "    (s)-[:mostSameStarFilm]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("f1", result.get(0)[1]);
  }

  @Test
  public void test8() {
    FileMutex.runTestWithMutex(this::doTest8);
  }

  private void doTest8() {
    String dsl =
        "Define (s:Film)-[p:lessSameStarFilm]->(o:Film) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[sf:starOfFilm]->(star:FilmStar)<-[sf2:starOfFilm]-(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        sameStarNum(\"得到相同男演员数目\") = group(s,o).countIf(star.gender == '男', star)\n"
            + "        R1(\"值取最大值\"): group(s).asc(sameStarNum).limit(1)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s)-[:lessSameStarFilm]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("f3", result.get(0)[1]);
  }

  @Test
  public void test9() {
    FileMutex.runTestWithMutex(this::doTest9);
  }

  private void doTest9() {
    String dsl =
        "Define (s:Film)-[p:firstSameStarFilm]->(o:Film) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[sf:starOfFilm]->(star:FilmStar)<-[sf2:starOfFilm]-(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R1(\"值取最大值\"): group(s).asc(sf2.joinTs).limit(1)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s)-[:firstSameStarFilm]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("f3", result.get(0)[1]);
  }

  @Test
  public void test10() {
    FileMutex.runTestWithMutex(this::doTest10);
  }

  private void doTest10() {
    String dsl =
        "Define (s:Film)-[p:lastSameStarFilm]->(o:Film) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[sf:starOfFilm]->(star:FilmStar)<-[sf2:starOfFilm]-(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R1(\"值取最大值\"): group(s).top(sf2.joinTs, 1)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    s [Film, __start__='true']\n"
            + "    o [Film]\n"
            + "    s->o [lastSameStarFilm]\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("f1", result.get(0)[1]);
  }

  @Test
  public void test11() {
    FileMutex.runTestWithMutex(this::doTest11);
  }

  private void doTest11() {
    String dsl =
        "Define (s:Film)-[p:lastSameStarFilm]->(o:Film) {\n"
            + "\tGraphStructure {\n"
            + "        (s)-[sf:starOfFilm]->(star:FilmStar)<-[sf2:starOfFilm]-(o)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R1(\"值取最大值\"): top(sf2.joinTs, 1)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s)-[:lastSameStarFilm]->(o)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("f1", result.get(0)[1]);
  }

  @Test
  public void test12() {
    FileMutex.runTestWithMutex(this::doTest12);
  }

  private void doTest12() {
    String dsl =
        "\n"
            + "GraphStructure {\n"
            + " s [Film, __start__='true']\n"
            + " star [FilmStar]\n"
            + " o [Film]\n"
            + " s->star [starOfFilm] as sf \n"
            + " o->star [starOfFilm] as sf2\n"
            + "}\n"
            + "Rule {\n"
            + "total = cast_type(sf.joinTs, 'bigint') + cast_type(sf2.joinTs, 'bigint')\n"
            + "R2: top(total, 1)\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o.id, total)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("f1", result.get(0)[1]);
    Assert.assertEquals("700", result.get(0)[2]);
  }
}
