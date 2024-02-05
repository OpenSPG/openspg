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

package com.antgroup.openspg.reasoner.common.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Combinatorial iterator for example: input two list ["1", "2", "3"] ["A", "B"] output [["1",
 * "A"],["1", "B"],["2","A"],["2","B"],["3","A"],["3","B"]]
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
