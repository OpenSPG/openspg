/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Combinatorial iterator for example: input two list ["1", "2", "3"] ["A", "B"] output [["1",
 * "A"],["1", "B"],["2","A"],["2","B"],["3","A"],["3","B"]]
 *
 * @author donghai.ydh
 * @version CombinationIterator.java, v 0.1 2023年04月21日 17:23 donghai.ydh
 */
public class CombinationIterator<T> implements Iterator<List<T>> {

  private final List<? extends Iterable<T>> lists;

  private final Stack<Iterator<T>> iteratorStack = new Stack<>();

  private final Stack<T> resultStack = new Stack<>();

  public CombinationIterator(List<? extends Iterable<T>> lists) {
    this.lists = lists;
    initIteratorStack();
  }

  @Override
  public boolean hasNext() {
    while (!iteratorStack.isEmpty()) {
      Iterator<T> peekIt = iteratorStack.peek();
      if (peekIt.hasNext()) {
        resultStack.push(peekIt.next());
        if (resultStack.size() == lists.size()) {
          return true;
        }
        initIteratorStack();
        continue;
      }
      if (1 == iteratorStack.size()) {
        return false;
      }
      iteratorStack.pop();
      resultStack.pop();
    }
    return false;
  }

  private void initIteratorStack() {
    for (int i = iteratorStack.size(); i < lists.size(); ++i) {
      Iterator<T> it = lists.get(i).iterator();
      iteratorStack.push(it);
      if ((i + 1) == lists.size()) {
        break;
      }
      resultStack.push(it.next());
    }
  }

  @Override
  public List<T> next() {
    if (resultStack.size() < lists.size()) {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
    }
    try {
      List<T> result = new ArrayList<>(resultStack);
      return result;
    } finally {
      resultStack.pop();
    }
  }
}
