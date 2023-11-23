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

package com.antgroup.openspg.common.util.struct.tuple;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Tuple2<T1, T2> {

  /** first element */
  public final T1 first;

  /** second element */
  public final T2 second;

  public Tuple2(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }

  public static <T1, T2> Tuple2<T1, T2> of(T1 first, T2 second) {
    return new Tuple2<>(first, second);
  }
}
