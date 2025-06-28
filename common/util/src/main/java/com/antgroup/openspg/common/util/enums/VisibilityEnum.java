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

public enum VisibilityEnum {

  /** public read */
  PUBLIC_READ,

  /** private */
  PRIVATE;

  /**
   * get visibility enum by visibility
   *
   * @param visibility
   * @return
   */
  public static VisibilityEnum getVisibilityEnum(String visibility) {
    for (VisibilityEnum visibilityEnum : VisibilityEnum.values()) {
      if (visibilityEnum.name().equals(visibility)) {
        return visibilityEnum;
      }
    }
    throw new IllegalArgumentException("Unsupported visibility:" + visibility);
  }
}
