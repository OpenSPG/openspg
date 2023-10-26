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

package com.antgroup.openspg.common.util;

import com.antgroup.openspg.api.facade.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

    public static String toJsonString(Object object) {
        String result = null;
        try {
            result = JSON.serialize(object);
        } catch (Exception e) {
            log.error("toJsonString error, object={}", object, e);
            result = StringUtils.toString(object);
        }
        return result;
    }
}
