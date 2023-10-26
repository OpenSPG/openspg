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

package com.antgroup.openspg.common.util.thread;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SPGThread extends Thread {

    public final static UncaughtExceptionHandler UncaughtExceptionHandler =
        (t, e) -> handleException(t.getName(), e);

    public SPGThread(String threadName) {
        this(threadName, null);
    }

    public SPGThread(String threadName, Runnable target) {
        super(target, threadName);
        setUncaughtExceptionHandler(UncaughtExceptionHandler);
    }

    /**
     * This will be used by the uncaught exception handler and just log a warning message and return.
     *
     * @param thName - thread name
     * @param e      - exception object
     */
    protected static void handleException(String thName, Throwable e) {
        log.warn("Exception occurred from thread {}", thName, e);
    }
}
