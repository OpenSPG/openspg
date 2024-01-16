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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util

import com.antgroup.openspg.builder.test.RiskMiningRecord
import com.antgroup.openspg.cloudext.interfaces.graphstore.RiskMiningLPGRecord
import spock.lang.Specification

class VertexRecordConvertorTest extends Specification {

    def testToVertexRecord() {
        expect:
        outputRecord == VertexRecordConvertor.toVertexRecord(inputRecord)

        where:
        inputRecord                                || outputRecord
        RiskMiningRecord.PERSON_RECORD1_NORMALIZED || RiskMiningLPGRecord.PERSON_RECORD1
    }
}
