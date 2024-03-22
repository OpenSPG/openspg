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
