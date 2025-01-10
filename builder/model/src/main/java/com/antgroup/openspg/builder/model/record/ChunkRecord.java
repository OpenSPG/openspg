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

package com.antgroup.openspg.builder.model.record;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ChunkRecord extends BaseRecord {

  private final Chunk chunk;

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }

  @Data
  public static class Chunk {
    private final String header;
    private final String name;
    private final String id;
    private final String content;
    private final String type;
    private final String summary;
    private final String textIndex;
    private final String vecIndex;

    @JSONField(serialize = false)
    public String getShortId() {
      if (StringUtils.isBlank(id)) {
        return "";
      }
      Integer length = 6;
      if (length >= id.length()) {
        return id;
      }
      return id.substring(0, 6);
    }
  }
}
