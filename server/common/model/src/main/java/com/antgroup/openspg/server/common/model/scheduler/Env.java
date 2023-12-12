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
 * Env
 *
 * @author yangjin @Title: Env.java @Description:
 */
public enum Env {

  /** local */
  LOCAL("local"),

  /** single */
  SINGLE("single"),

  /** dev */
  DEV("dev"),

  /** stable */
  STABLE("stable"),

  /** pre */
  PRE("pre"),

  /** gray */
  GRAY("gray"),

  /** prod */
  PROD("prod");

  private String value;

  Env(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  /**
   * get by name
   *
   * @param name
   * @return
   */
  public static Env getByName(String name, Env defaultValue) {
    for (Env value : Env.values()) {
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
  public static Env getByName(String name) {
    return getByName(name, null);
  }
}
