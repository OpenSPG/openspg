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

package com.antgroup.openspg.server.common.model.scheduler;

/**
 * Status
 *
 * @author yangjin @Title: Status.java @Description:
 */
public enum Status {
  /** online */
  ONLINE,
  /** offline */
  OFFLINE;

  /**
   * get by name
   *
   * @param name
   * @return
   */
  public static Status getByName(String name, Status defaultValue) {
    for (Status value : Status.values()) {
      if (value.name().equalsIgnoreCase(name)) {
        return value;
      }
    }
    return defaultValue;
  }

  /**
   * get by name
   *
   * @param name
   * @return
   */
  public static Status getByName(String name) {
    return getByName(name, null);
  }
}
