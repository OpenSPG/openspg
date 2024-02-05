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

package com.antgroup.openspg.reasoner.common;

import com.antgroup.openspg.reasoner.common.utils.CombinationIterator;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class CombinationIteratorTest {

  @Test
  public void combinationTest1() {
    List<List<String>> lists =
        Lists.newArrayList(Lists.newArrayList("1", "2", "3"), Lists.newArrayList("A", "B"));
    int resultCount = 1;
    for (List<String> list : lists) {
      resultCount *= list.size();
    }
    CombinationIterator<String> it = new CombinationIterator<>(lists);
    for (int i = 0; i < resultCount; ++i) {
      List<String> combination = it.next();
      System.out.println(combination);
      Assert.assertEquals(2, combination.size());
      if (0 == i) {
        Assert.assertEquals("1", combination.get(0));
        Assert.assertEquals("A", combination.get(1));
      }
    }
    Assert.assertFalse(it.hasNext());
  }

  @Test
  public void combinationTest2() {
    List<List<Integer>> lists =
        Lists.newArrayList(
            Lists.newArrayList(1, 2, 2), Lists.newArrayList(21, 22), Lists.newArrayList(31, 32));
    int resultCount = 1;
    for (List<Integer> list : lists) {
      resultCount *= list.size();
    }
    CombinationIterator<Integer> it = new CombinationIterator<>(lists);
    for (int i = 0; i < resultCount; ++i) {
      List<Integer> combination = it.next();
      System.out.println(combination);
      Assert.assertEquals(3, combination.size());
      if (1 == i) {
        Assert.assertEquals(1, (int) combination.get(0));
        Assert.assertEquals(21, (int) combination.get(1));
        Assert.assertEquals(32, (int) combination.get(2));
      }
    }
    Assert.assertFalse(it.hasNext());
  }
}
