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

package com.antgroup.openspg.builder.model.record.property

import spock.lang.Specification

class SPGPropertyValueTest extends Specification {

    def "testGetStdValue"() {
        given:
        def value = new SPGPropertyValue("bd8cc63024c7a1d4adb3d08f0f4d4827,1ef0e106653b871d429cfab0cd5ea70a")
        value.setStds(["bd8cc63024c7a1d4adb3d08f0f4d4827", "1ef0e106653b871d429cfab0cd5ea70a"])

        expect:
        value.getStdValue() == 'bd8cc63024c7a1d4adb3d08f0f4d4827,1ef0e106653b871d429cfab0cd5ea70a'
    }

    def "testMerge"() {
        expect:
        def otherValue = new SPGPropertyValue("bd8cc63024c7a1d4adb3d08f0f4d4827")
        value.merge(otherValue)
        value.getRaw() == expectRaw

        where:
        value                                                    || expectRaw
        new SPGPropertyValue(null)                               || "bd8cc63024c7a1d4adb3d08f0f4d4827"
        new SPGPropertyValue("1ef0e106653b871d429cfab0cd5ea70a") || "1ef0e106653b871d429cfab0cd5ea70a,bd8cc63024c7a1d4adb3d08f0f4d4827"
    }
}
