/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.primitives;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author donghai.ydh
 * @version Bytes.java, v 0.1 2023年04月25日 19:58 donghai.ydh
 */
public class Bytes implements Serializable {

  private final byte[] bytes;

  public Bytes(byte[] bytes) {
    this.bytes = bytes;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.bytes);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Bytes)) {
      return false;
    }
    Bytes that = (Bytes) obj;
    return Arrays.equals(this.bytes, that.bytes);
  }

  /**
   * Getter method for property <tt>bytes</tt>.
   *
   * @return property value of bytes
   */
  public byte[] getBytes() {
    return bytes;
  }
}
