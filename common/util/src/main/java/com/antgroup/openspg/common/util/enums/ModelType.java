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

public enum ModelType {
  MAAS("maas", "maas模型"),
  OLLAMA("ollama", "SPG模型"),
  VLLM("vllm", "vllm模型");
  private String code;
  private String desc;

  public static ModelType getByCode(String code) {
    for (ModelType modelType : values()) {
      if (modelType.getCode().equals(code)) {
        return modelType;
      }
    }
    return null;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  ModelType(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }
}
