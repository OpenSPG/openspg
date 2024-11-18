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

package com.antgroup.openspg.server.infra.dao.repository.schema.enums;

/** True or false enum. */
public enum TrueOrFalseEnum {
  /** TRUE */
  TRUE,

  /** FALSE */
  FALSE;

  /**
   * If the value is true.
   *
   * @param val
   * @return
   */
  public static boolean isTrue(String val) {
    return TrueOrFalseEnum.TRUE.name().equalsIgnoreCase(val);
  }

  /**
   * If the value is 1.
   *
   * @param val
   * @return
   */
  public static Boolean isOne(Integer val) {
    return new Integer(1).equals(val);
  }

  public static TrueOrFalseEnum parse(boolean isTrue) {
    return isTrue ? TRUE : FALSE;
  }
}
