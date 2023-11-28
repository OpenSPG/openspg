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


package com.antgroup.openspg.cloudext.interfaces.searchengine.impl

import spock.lang.Specification

class DefaultIdxNameConvertorTest extends Specification {

    static def convert = new DefaultIdxNameConvertor()

    def "testConvertIdxName"() {
        expect:
        converted == convert.convertIdxName(idxName)
        def restored = convert.restoreIdxName(converted)
        idxName == restored

        where:
        idxName          || converted
        "FraudTest1.App" || "\$fraud\$test1-\$app"
        "FraudTest1.APP" || "\$fraud\$test1-\$a\$p\$p"
        "ALIPAY.APP"     || "\$a\$l\$i\$p\$a\$y-\$a\$p\$p"
    }
}
