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

package com.antgroup.openspg.server.schema.core.model.predicate;

/** Enumeration of property encryption methods */
public enum EncryptTypeEnum {

  /** Identifier card. */
  IDENTITY_CARD("identityCard"),

  /** Mobile number. */
  MOBILE("mobile"),

  /** Email address. */
  EMAIL("email"),

  /** Bank card */
  BANK_CARD("bankCard"),

  /** IP or Mac Address */
  IP_OR_MAC("ipOrMac"),

  /** User name */
  USERNAME("userName"),

  /** Car number */
  CARNO("carNo"),

  /** Vin number */
  VINNO("vinNo"),

  /** User address */
  ADDRESS("address"),

  /** Universal encryption method. */
  COMMON("common"),

  /** No encryption */
  NONE("NONE");

  private final String type;

  EncryptTypeEnum(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static EncryptTypeEnum toEnum(String type) {
    for (EncryptTypeEnum maskTypeEnum : EncryptTypeEnum.values()) {
      if (maskTypeEnum.getType().equalsIgnoreCase(type)) {
        return maskTypeEnum;
      }
    }
    return EncryptTypeEnum.NONE;
  }
}
