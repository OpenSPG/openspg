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
