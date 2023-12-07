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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.primitives;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author donghai.ydh
 * @version Bytes.java, v 0.1 2023-04-25 19:58 donghai.ydh
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
