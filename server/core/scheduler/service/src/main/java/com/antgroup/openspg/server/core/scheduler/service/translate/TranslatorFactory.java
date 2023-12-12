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

/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.translate;

import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Translator Factory. get Translate Bean by type */
public class TranslatorFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(TranslatorFactory.class);

  /** get Translate by type */
  public static Translate getTranslator(String type) {
    String translator = getTranslatorNameByType(type);
    Translate dagTranslate = SpringContextHolder.getBean(translator, Translate.class);
    if (dagTranslate == null) {
      LOGGER.error(String.format("getTranslator bean error type:%s", type));
      throw new RuntimeException("not find bean type:" + type);
    }
    return dagTranslate;
  }

  /** get Translate name by type */
  private static String getTranslatorNameByType(String type) {
    TranslateEnum translate = TranslateEnum.getByName(type);
    if (translate == null) {
      throw new RuntimeException("TranslateEnum Not exist:" + type);
    }
    return translate.getType();
  }
}
