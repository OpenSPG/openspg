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

package com.antgroup.openspg.server.core.scheduler.service.translate;

/**
 * Translate Enum
 *
 * @author yangjin
 * @Title: TranslateEnum.java
 * @Description:
 */
public enum TranslateEnum {
    /**
     * local dry run
     */
    LOCAL_DRY_RUN("localDryRunTranslate", "本地空跑任务"),
    /**
     * builder
     */
    KG_BUILDER("builderTranslate", "知识加工任务");

    private String type;
    private String description;

    TranslateEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static TranslateEnum getByName(String name, TranslateEnum defaultValue) {
        for (TranslateEnum value : TranslateEnum.values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static TranslateEnum getByName(String name) {
        return getByName(name, null);
    }
}
