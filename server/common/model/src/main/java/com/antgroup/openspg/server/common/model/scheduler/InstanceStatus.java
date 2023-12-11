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
 * Instance Status
 *
 * @author yangjin
 * @Title: InstanceStatus.java
 * @Description:
 */
public enum InstanceStatus {
    /**
     * WAITING
     */
    WAITING,
    /**
     * running
     */
    RUNNING,
    /**
     * skip
     */
    SKIP,
    /**
     * finish
     */
    FINISH,
    /**
     * terminate
     */
    TERMINATE,
    /**
     * set finish
     */
    SET_FINISH,
    /**
     * all done
     */
    ALL_DONE;

    /**
     * status is Finish
     *
     * @param status
     * @return
     */
    public static boolean isFinish(String status) {
        return InstanceStatus.FINISH.name().equalsIgnoreCase(status) || InstanceStatus.TERMINATE.name().equalsIgnoreCase(status)
                || InstanceStatus.SET_FINISH.name().equalsIgnoreCase(status) || InstanceStatus.ALL_DONE.name().equalsIgnoreCase(status)
                || InstanceStatus.SKIP.name().equalsIgnoreCase(status);
    }

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static InstanceStatus getByName(String name, InstanceStatus defaultValue) {
        for (InstanceStatus value : InstanceStatus.values()) {
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
    public static InstanceStatus getByName(String name) {
        return getByName(name, null);
    }
}
