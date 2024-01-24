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

package com.antgroup.openspg.reasoner.rdg.common;

import java.io.Serializable;
import java.util.Arrays;

public class GroupByKeyItem implements Serializable {
  private final Object[] keys;

  public GroupByKeyItem(Object[] keys) {
    this.keys = keys;
  }

  public Object[] getKeys() {
    return keys;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(keys);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GroupByKeyItem)) {
      return false;
    }
    GroupByKeyItem other = (GroupByKeyItem) obj;
    return Arrays.equals(this.keys, other.keys);
  }
}
