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

public enum PermissionEnum {
  SUPER(1),
  OWNER(2),
  MEMBER(3);

  long id;

  PermissionEnum(long id) {
    this.id = id;
  }

  /**
   * get roleType by value
   *
   * @param value
   * @return
   */
  public static PermissionEnum getRoleType(String value) {
    for (PermissionEnum roleTypeEnum : PermissionEnum.values()) {
      if (roleTypeEnum.name().equals(value)) {
        return roleTypeEnum;
      }
    }
    throw new IllegalArgumentException("Unsupported roleType type value:" + value);
  }

  /**
   * get roleType by id
   *
   * @param id
   * @return
   */
  public static PermissionEnum getRoleTypeById(long id) {
    for (PermissionEnum roleTypeEnum : PermissionEnum.values()) {
      if (roleTypeEnum.getId() == id) {
        return roleTypeEnum;
      }
    }
    throw new IllegalArgumentException("Unsupported roleType type id:" + id);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
