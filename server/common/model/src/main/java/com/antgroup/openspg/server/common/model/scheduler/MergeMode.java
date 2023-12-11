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

package com.antgroup.openspg.server.common.model.scheduler;

/**
 * Merge Mode
 *
 * @author yangjin
 * @Title: MergeMode.java
 * @Description:
 */
public enum MergeMode {
    /**
     * merge
     */
    MERGE,
    /**
     * snapshot
     */
    SNAPSHOT;

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static MergeMode getByName(String name, MergeMode defaultValue) {
        for (MergeMode value : MergeMode.values()) {
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
    public static MergeMode getByName(String name) {
        return getByName(name, null);
    }
}
