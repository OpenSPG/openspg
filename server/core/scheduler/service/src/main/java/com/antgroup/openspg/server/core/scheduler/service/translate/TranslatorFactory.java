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
package com.antgroup.openspg.server.core.scheduler.service.translate;

import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TranslateType;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

/** Translator Factory. get Translate Bean by type */
@Slf4j
public class TranslatorFactory {

  /** get Translate by type */
  public static Translate getTranslator(TranslateType type) {
    Translate dagTranslate = SpringContextHolder.getBean(type.getType(), Translate.class);
    if (dagTranslate == null) {
      log.error("getTranslator bean error type:{}", type);
      throw new RuntimeException("not find bean type:" + type);
    }
    return dagTranslate;
  }
}
