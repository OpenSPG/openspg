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

package com.antgroup.openspg.reasoner.runner.local;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalRunnerMain {

  /** result */
  public static LocalReasonerResult result = null;

  /** KGReasoner main */
  public static void main(String[] args) {
    String taskInfoJson = new String(Base64.getDecoder().decode(args[0]), StandardCharsets.UTF_8);
    LocalReasonerTask task = JSON.parseObject(taskInfoJson, LocalReasonerTask.class);
    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    result = runner.run(task);
    if (null != result) {
      log.info(result.toString());
    }
  }
}
