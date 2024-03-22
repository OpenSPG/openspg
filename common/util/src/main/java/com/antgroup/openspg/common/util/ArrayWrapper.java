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

package com.antgroup.openspg.common.util;

import java.io.Serializable;
import java.util.Arrays;

public class ArrayWrapper implements Serializable {
  private final Object[] value;

  public ArrayWrapper(Object[] value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ArrayWrapper)) {
      return false;
    }
    ArrayWrapper other = (ArrayWrapper) obj;
    return Arrays.equals(this.value, other.value);
  }

  public Object[] getValue() {
    return value;
  }
}
