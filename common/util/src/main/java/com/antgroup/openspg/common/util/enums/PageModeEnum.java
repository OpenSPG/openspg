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
package com.antgroup.openspg.common.util.enums;

public enum PageModeEnum {

  /** ALL */
  ALL,

  /** SINGLE */
  SINGLE,
  ;

  /**
   * get by name
   *
   * @param name
   * @return
   */
  public static PageModeEnum getByName(String name) {
    for (PageModeEnum mode : values()) {
      if (mode.name().equals(name)) {
        return mode;
      }
    }
    throw new IllegalArgumentException("Unsupported page model:" + name);
  }
}
