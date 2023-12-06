/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common;

import com.antgroup.openspg.reasoner.common.utils.CombinationIterator;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author donghai.ydh
 * @version CombinationIteratorTest.java, v 0.1 2023年04月21日 18:09 donghai.ydh
 */
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
