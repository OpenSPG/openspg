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

package com.antgroup.openspg.reasoner.io.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadRange implements Comparable<ReadRange> {
  private long start;
  private long end;

  /** create read range */
  public ReadRange(long start, long end) {
    this.start = start;
    this.end = end;
  }

  /** get read count */
  public long getCount() {
    return end - start;
  }

  /** compare */
  @Override
  public int compareTo(ReadRange o) {
    return (int) (this.start - o.start);
  }
}
