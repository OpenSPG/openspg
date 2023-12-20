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

package com.antgroup.openspg.reasoner.runner.local.main;

import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.runner.local.main.basetest.FilmBaseTestData;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KgReasonerDependencyFilmTest {

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

  public Catalog getMockCatalogSchema() {
    return FilmBaseTestData.getMockCatalogSchema();
  }

  private List<String[]> runTestResult(String dsl) {
    return FilmBaseTestData.runTestResult(dsl);
  }

  @Test
  public void test1() {
    FileMutex.runTestWithMutex(this::doTest1);
  }

  private void doTest1() {
    String dsl =
        "GraphStructure {\n"
            + "\t(s:Film)-[p]->(u)\n"
            + "}\n"
            + "Rule {\n"
            + "\tnums = group(s).count(u)\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id as id_num, nums as tmp)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("4", result.get(0)[1]);
  }

  @Test
  public void test2() {
    FileMutex.runTestWithMutex(this::doTest2);
  }

  private void doTest2() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, s.starNum as starNum)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("3", result.get(0)[1]);
  }

  @Test
  public void test3() {
    FileMutex.runTestWithMutex(this::doTest3);
  }

  private void doTest3() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, s.starNum, s.directNums)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("3", result.get(0)[1]);
    Assert.assertEquals("1", result.get(0)[2]);
  }

  @Test
  public void test4() {
    FileMutex.runTestWithMutex(this::doTest4);
  }

  private void doTest4() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:workerNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p]->(u)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, s.starNum, s.directNums, s.workerNums)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("3", result.get(0)[1]);
    Assert.assertEquals("1", result.get(0)[2]);
    Assert.assertEquals("4", result.get(0)[3]);
  }

  @Test
  public void test5() {
    FileMutex.runTestWithMutex(this::doTest5);
  }

  private void doTest5() {
    String dsl =
        "Define (s:FilmStar)-[p:isHosStar]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:starOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).count(u)\n"
            + "\to = rule_value(filmNum > 2, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:hotStarNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).countIf(u.isHosStar == true, u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, s.hotStarNum)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("3", result.get(0)[1]);
  }

  @Test
  public void test6() {
    FileMutex.runTestWithMutex(this::doTest6);
  }

  private void doTest6() {
    String dsl =
        "Define (s:FilmStar)-[p:isHosStar]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:starOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).count(u)\n"
            + "\to = rule_value(filmNum > 2, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:hotStarNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).countIf(u.isHosStar == true, u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, s.hotStarNum, s.directNums)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("3", result.get(0)[1]);
    Assert.assertEquals("1", result.get(0)[2]);
  }

  @Test
  public void test7() {
    FileMutex.runTestWithMutex(this::doTest7);
  }

  private void doTest7() {
    String dsl =
        "Define (s:FilmStar)-[p:isHosStar]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:starOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).count(u)\n"
            + "\to = rule_value(filmNum > 2, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:hotStarNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).countIf(u.isHosStar == true, u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:FilmDirector)-[p:isHotDirector]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:directOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).count(u)\n"
            + "\to = rule_value(filmNum > 1, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:hasHotDirector]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\tnum = group(s).countIf(u.isHotDirector == true, u)\n"
            + "\to = rule_value(num > 0, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, s.hotStarNum, s.hasHotDirector)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("3", result.get(0)[1]);
    Assert.assertEquals("true", result.get(0)[2]);
  }

  @Test
  public void test8() {
    FileMutex.runTestWithMutex(this::doTest8);
  }

  private void doTest8() {
    String dsl =
        "Define (s:Film)-[p:hasManyStar]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\tnum = group(s).count(u)\n"
            + "\to = rule_value(num > 2, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "\n"
            + "Define (s:FilmStar)-[p:partInFamous]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:starOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).countIf(u.hasManyStar == true, u)\n"
            + "\to = rule_value(filmNum > 0, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:FilmDirector)-[p:partInFamous]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:directOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).countIf(u.hasManyStar == true, u)\n"
            + "\to = rule_value(filmNum > 0, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm|directOfFilm]->(u:FilmStar|FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, u.partInFamous)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(5, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("true", result.get(0)[1]);
  }

  @Test
  public void test9() {
    FileMutex.runTestWithMutex(this::doTest9);
  }

  private void doTest9() {
    String dsl =
        "Define (s:FilmStar)-[p:isHosStar]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:starOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).count(u)\n"
            + "\to = rule_value(filmNum > 2, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:hotStarNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).countIf(u.isHosStar == true, u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, s.hotStarNum, s.starNum, s.directNums)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("3", result.get(0)[1]);
    Assert.assertEquals("3", result.get(0)[2]);
    Assert.assertEquals("1", result.get(0)[3]);
  }

  @Test
  public void test10() {
    FileMutex.runTestWithMutex(this::doTest10);
  }

  private void doTest10() {
    String dsl =
        "Define (s:Film)-[p:hasManyStar]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\tnum = group(s).count(u)\n"
            + "\to = rule_value(num > 2, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "\n"
            + "Define (s:FilmStar)-[p:partInFamous]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:starOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).countIf(u.hasManyStar == true, u)\n"
            + "\to = rule_value(filmNum > 0, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:FilmDirector)-[p:partInFamous]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:directOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).countIf(u.hasManyStar == true, u)\n"
            + "\to = rule_value(filmNum > 0, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm|directOfFilm]->(u:FilmStar|FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, u.partInFamous, s.directNums)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(5, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("true", result.get(0)[1]);
    Assert.assertEquals("1", result.get(0)[2]);
  }

  @Test
  public void test11() {
    FileMutex.runTestWithMutex(this::doTest11);
  }

  private void doTest11() {
    String dsl =
        "Define (s:Film)-[p:hasManyStar]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\tnum = group(s).count(u)\n"
            + "\to = rule_value(num > 2, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "\n"
            + "Define (s:FilmStar)-[p:partInFamous]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:starOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).countIf(u.hasManyStar == true, u)\n"
            + "\to = rule_value(filmNum > 0, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:FilmDirector)-[p:partInFamous]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:directOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).countIf(u.hasManyStar == true, u)\n"
            + "\to = rule_value(filmNum > 0, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p]->(u)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,s.hasManyStar, u.partInFamous)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(5, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("true", result.get(0)[1]);
    Assert.assertEquals("true", result.get(0)[2]);
  }

  @Test
  public void test12() {
    FileMutex.runTestWithMutex(this::doTest12);
  }

  private void doTest12() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,s.partiInfo)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("star:3 director:1", result.get(0)[1]);
  }

  @Test
  public void test13() {
    FileMutex.runTestWithMutex(this::doTest13);
  }

  private void doTest13() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:FilmStar)-[p:isHosStar]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:starOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).count(u)\n"
            + "\to = rule_value(filmNum > 2, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,s.partiInfo, u.isHosStar)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("star:3 director:1", result.get(0)[1]);
    Assert.assertEquals("true", result.get(0)[2]);
  }

  @Test
  public void test14() {
    FileMutex.runTestWithMutex(this::doTest14);
  }

  private void doTest14() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:FilmStar)-[p:partInFamous]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "\t(s)<-[p:starOfFilm]-(u:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "\tfilmNum = group(s).countIf(u.starNum>1, u)\n"
            + "\to = rule_value(filmNum > 0, true, false)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,s.partiInfo, u.partInFamous)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("star:3 director:1", result.get(0)[1]);
    Assert.assertEquals("true", result.get(0)[2]);
  }

  @Test
  public void test15() {
    FileMutex.runTestWithMutex(this::doTest15);
  }

  private void doTest15() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo2]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,s.partiInfo, s.partiInfo2)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(3, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("star:3 director:1", result.get(0)[1]);
    Assert.assertEquals("star:3 director:1", result.get(0)[2]);
  }

  @Test
  public void test16() {
    FileMutex.runTestWithMutex(this::doTest16);
  }

  private void doTest16() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,s.partiInfo, s.starNum, s.directNums)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("star:3 director:1", result.get(0)[1]);
    Assert.assertEquals("3", result.get(0)[2]);
    Assert.assertEquals("1", result.get(0)[3]);
  }

  @Test
  public void test17() {
    FileMutex.runTestWithMutex(this::doTest17);
  }

  private void doTest17() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:starInfo]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"starInfo\", s.starNum)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,s.partiInfo, s.starNum, s.starInfo)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("star:3 director:1", result.get(0)[1]);
    Assert.assertEquals("3", result.get(0)[2]);
    Assert.assertEquals("starInfo3", result.get(0)[3]);
  }

  @Test
  public void test18() {
    FileMutex.runTestWithMutex(this::doTest18);
  }

  private void doTest18() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo2]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,s.partiInfo, s.starNum, s.partiInfo2)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("star:3 director:1", result.get(0)[1]);
    Assert.assertEquals("3", result.get(0)[2]);
    Assert.assertEquals("star:3 director:1", result.get(0)[3]);
  }

  @Test
  public void test19() {
    FileMutex.runTestWithMutex(this::doTest19);
  }

  private void doTest19() {
    String dsl =
        "Define (s:Film)-[p:starNum]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:directNums]->(o:Int) {\n"
            + "GraphStructure {\n"
            + "\t(s)-[p:directOfFilm]->(u:FilmDirector)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = group(s).count(u)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo2]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:Film)-[p:partiInfo3]->(o:String) {\n"
            + "GraphStructure {\n"
            + "\t(s)\n"
            + "}\n"
            + "Rule {\n"
            + "\to = concat(\"star:\", s.starNum, \" director:\", s.directNums)\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Film)-[p:starOfFilm]->(u:FilmStar)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,s.partiInfo, s.partiInfo3, s.partiInfo2)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(4, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("star:3 director:1", result.get(0)[1]);
    Assert.assertEquals("star:3 director:1", result.get(0)[2]);
    Assert.assertEquals("star:3 director:1", result.get(0)[2]);
  }

  @Test
  public void test20() {
    FileMutex.runTestWithMutex(this::doTest20);
  }

  private void doTest20() {
    String dsl =
        "GraphStructure {\n"
            + "\t(s:Film)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id as id_num, s.filmBudget)\n"
            + "}";
    List<String[]> result = runTestResult(dsl);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(2, result.get(0).length);
    Assert.assertEquals("root", result.get(0)[0]);
    Assert.assertEquals("100", result.get(0)[1]);
  }
}
