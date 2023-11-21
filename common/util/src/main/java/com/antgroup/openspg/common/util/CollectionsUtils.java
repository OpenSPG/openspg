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

package com.antgroup.openspg.common.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class CollectionsUtils {

  private CollectionsUtils() {}

  /**
   * 将inputs列表每个元素进行func处理得到新的列表
   *
   * @param inputs 输入列表
   * @param func 执行方法
   * @param <I> 输入类型
   * @param <O> 输出类型
   * @return 执行结果
   */
  public static <I, O> List<O> listMap(Collection<I> inputs, Function<I, O> func) {
    if (CollectionUtils.isEmpty(inputs)) {
      return new ArrayList<>(0);
    }

    return inputs.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList());
  }

  /**
   * 将inputs集合每个元素进行func处理得到新的集合
   *
   * @param inputs 输入集合
   * @param func 执行方法
   * @param <I> 输入类型
   * @param <O> 输出类型
   * @return 执行结果
   */
  public static <I, O> Set<O> setMap(Collection<I> inputs, Function<I, O> func) {
    if (CollectionUtils.isEmpty(inputs)) {
      return new HashSet<>(0);
    }

    return inputs.stream().map(func).filter(Objects::nonNull).collect(Collectors.toSet());
  }

  /**
   * 将inputs集合每个元素进行func过滤后得到新的集合
   *
   * @param inputs 输入集合
   * @param func 执行方法
   * @param <I> 输入类型
   * @return 执行结果
   */
  public static <I> List<I> listFilter(Collection<I> inputs, Predicate<I> func) {
    if (CollectionUtils.isEmpty(inputs)) {
      return new ArrayList<>(0);
    }

    return inputs.stream().filter(func).filter(Objects::nonNull).collect(Collectors.toList());
  }

  public static <I> List<I> defaultEmpty(List<I> inputs) {
    if (inputs == null) {
      return Collections.emptyList();
    }
    return inputs;
  }

  /**
   * 转化为list
   *
   * @param elements 元素列表
   * @param <E> 元素类型
   * @return list
   */
  public static <E> List<E> asList(E... elements) {
    return Lists.newArrayList(elements);
  }
}
