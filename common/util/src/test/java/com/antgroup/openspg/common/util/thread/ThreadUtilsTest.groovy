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


package com.antgroup.openspg.common.util.thread

import spock.lang.Specification

/**
 *  */
class ThreadUtilsTest extends Specification {

    def testNThreads() {
        expect:
        expectedResult == ThreadUtils.nThreads(nThreads)

        where:
        nThreads || expectedResult
        "*1"     || Runtime.getRuntime().availableProcessors()
        "*2"     || 2 * Runtime.getRuntime().availableProcessors()
        "5"      || 5
        "10"     || 10
    }
}
