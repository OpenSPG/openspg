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

public enum ModelTypeEnum {
  LLM("chat", "大语言模型"),
  EMBEDDING("embedding", "向量模型"),
  RERANKER("reranker", "重排模型"),
  TEXT_TO_IMAGE("text-to-image", "图片生成"),
  IMAGE_TO_IMAGE("image-to-image", "视觉模型"),
  SPEECH_TO_TEXT("speech-to-text", "语音识别"),
  TEXT_TO_VIDEO("text-to-video", "视频生成"),
  ;

  private String code;
  private String desc;

  ModelTypeEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public static ModelTypeEnum getByCode(String code) {
    for (ModelTypeEnum value : values()) {
      if (value.code.equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unsupported model type:" + code);
  }
}
