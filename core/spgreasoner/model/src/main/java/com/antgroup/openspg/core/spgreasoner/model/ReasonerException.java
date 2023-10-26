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

package com.antgroup.openspg.core.spgreasoner.model;

import com.antgroup.openspg.common.model.exception.OpenSPGException;


public class ReasonerException extends OpenSPGException {

    private ReasonerException(Throwable cause, String messagePattern, Object... args) {
        super(cause, true, true, messagePattern, args);
    }

    private ReasonerException(String messagePattern, Object... args) {
        this(null, messagePattern, args);
    }

    public static ReasonerException reasonerError(Throwable e) {
        return new ReasonerException(e, e.getMessage());
    }

    public static ReasonerException reasonerError(String errorMsg) {
        return new ReasonerException(errorMsg);
    }

    public static ReasonerException timeout(int minutes) {
        return new ReasonerException("reasoner cannot finish in {} minutes, please submit reasoner job", minutes);
    }
}
