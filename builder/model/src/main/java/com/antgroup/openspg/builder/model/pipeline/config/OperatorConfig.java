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

package com.antgroup.openspg.builder.model.pipeline.config;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import com.google.common.collect.Lists;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;

@Getter
@EqualsAndHashCode(callSuper = false)
public class OperatorConfig extends BaseValObj {

  private final String filePath;

  private final String modulePath;

  private final String className;

  private final String method;

  private final Map<String, String> params;

  private final String paramsPrefix;

  public OperatorConfig(
      String filePath,
      String modulePath,
      String className,
      String method,
      Map<String, String> params) {
    this(filePath, modulePath, className, method, params, "");
  }

  public OperatorConfig(
      String filePath,
      String modulePath,
      String className,
      String method,
      Map<String, String> params,
      String paramsPrefix) {
    this.filePath = filePath;
    this.modulePath = modulePath;
    this.className = className;
    this.method = method;
    this.params = params;
    this.paramsPrefix = paramsPrefix;
  }

  public String getUniqueKey() {
    return DigestUtils.md5Hex(
        String.join(
            ";",
            Lists.newArrayList(
                filePath, modulePath, className, method, JSON.toJSONString(params), paramsPrefix)));
  }
}
