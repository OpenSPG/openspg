package com.antgroup.openspg.cloudext.interfaces.graphstore

import com.antgroup.openspg.builder.test.RiskMiningRecord
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord
import spock.lang.Specification

class RiskMiningLPGRecord extends Specification {

    public final static VertexRecord PERSON_RECORD1 = new VertexRecord(
            RiskMiningRecord.PERSON_RECORD1.id,
            RiskMiningRecord.PERSON_RECORD1.getName(),
            [
                    new LPGPropertyRecord("name", "裘**"),
                    new LPGPropertyRecord("age", 58L),
                    new LPGPropertyRecord("hasPhone", "154****7458"),
            ]
    );
}
