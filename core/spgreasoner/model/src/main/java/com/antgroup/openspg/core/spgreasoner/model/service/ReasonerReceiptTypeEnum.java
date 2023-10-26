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

package com.antgroup.openspg.core.spgreasoner.model.service;

/**
 * The type of receipt returned by the knowledge reasoning engine.
 * <p>
 * Different reasoning modes produce different reasoning receipts. When reasoning locally, the reasoning receipt is the
 * reasoning result, which is usually expressed in the form of a table; in remote cluster reasoning, the reasoning
 * receipt may be a UUID, used for front-end continuous polling to obtain reasoning results
 */
public enum ReasonerReceiptTypeEnum {
    /**
     * When the reasoning engine finally executes a segment of KGDSL, the result is carried in a table data structure;
     */
    TABLE,

    /**
     * However, when a reasoning task is sent to a remote cluster, the reasoning result returned by the inference
     * service to the front-end may temporarily be a UUID, and the front-end will then obtain the real reasoning result
     * from the reasoning service with the UUID, that is, a table
     */
    JOB,
    ;
}
