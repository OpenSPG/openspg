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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model;

/** Tugraph property type enum. */
public enum DataTypeEnum {
  /** Int(8-bit) */
  INT8("int8"),

  /** Int(16-bit) */
  INT16("int16"),

  /** Int(32-bit) */
  INT32("int32"),

  /** Int(64-bit) */
  INT64("int64"),

  /** Float */
  FLOAT("float"),

  /** Double */
  DOUBLE("double"),

  /** String */
  STRING("string"),

  /** Date */
  DATE("date"),

  /** Date time */
  DATETIME("datetime"),

  /** Binary */
  BINARY("binary"),

  /** Boolean */
  BOOL("bool");

  /** The lowercase form */
  private String lowercaseForm;

  DataTypeEnum(String lowercaseForm) {
    this.lowercaseForm = lowercaseForm;
  }

  /** Parse tugraph property type. */
  public static DataTypeEnum parse(String type) {
    for (DataTypeEnum dataTypeEnum : DataTypeEnum.values()) {
      if (dataTypeEnum.name().equalsIgnoreCase(type)) {
        return dataTypeEnum;
      }
    }
    throw new RuntimeException("Parse tugraph data type failed, unexpected type=" + type);
  }

  public String getLowercaseForm() {
    return lowercaseForm;
  }
}
