package com.antgroup.openspg.cloudext.interfaces.graphstore

import com.antgroup.openspg.core.schema.model.BasicInfo
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.core.schema.model.predicate.Property
import com.antgroup.openspg.core.schema.model.type.EntityType
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo
import spock.lang.Specification

class RecordGenerator extends Specification {

    public final static ParentTypeInfo THING = new ParentTypeInfo(1L, 1L, SPGTypeIdentifier.parse("THING"), [])

    public final static EntityType PERSON = new EntityType(
            new BasicInfo<SPGTypeIdentifier>(SPGTypeIdentifier.parse("RiskMining.Person")),
            THING,
            [
                    new Property(
                            new BasicInfo<PredicateIdentifier>(new PredicateIdentifier(""))
                    )
            ]
    )
}
