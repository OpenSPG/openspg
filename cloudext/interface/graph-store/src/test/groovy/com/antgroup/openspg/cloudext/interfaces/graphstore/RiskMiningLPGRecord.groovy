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
