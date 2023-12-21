package com.antgroup.openspg.builder.core.operator.impl

import com.antgroup.openspg.builder.core.PropertyGenerator
import com.antgroup.openspg.builder.core.operator.linking.impl.BasicPropertyLinking
import com.antgroup.openspg.builder.model.exception.LinkingException
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord
import com.antgroup.openspg.builder.model.record.property.SPGPropertyValue
import com.antgroup.openspg.builder.model.record.property.SPGSubPropertyRecord
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum
import spock.lang.Specification

class BasicPropertyNormalizerTest extends Specification {

    static def normalizer = new BasicPropertyLinking()

    def testPropertyNormalize() {
        given:
        def propertyRecord = new SPGPropertyRecord(property, new SPGPropertyValue(rawValue))

        expect:
        normalizer.propertyLinking(propertyRecord)
        propertyRecord.getValue().getStds() == expectValues

        where:
        property               | rawValue || expectValues
        getProperty("Text")    | 'jack'   || ['jack']
        getProperty("Integer") | '100'    || [100]
        getProperty("Double")  | '1.1'    || [1.1]
    }

    def testPropertyNormalizeError() {
        given:
        def propertyRecord = new SPGPropertyRecord(property, new SPGPropertyValue(rawValue))

        when:
        normalizer.propertyLinking(propertyRecord)
        propertyRecord.getValue().getStds()

        then:
        thrown(LinkingException.class)

        where:
        property               | rawValue
        getProperty("Integer") | '1.1'
        getProperty("Double")  | 'xxx'
    }

    def testSubPropertyNormalize() {
        given:
        def subPropertyRecord = new SPGSubPropertyRecord(subProperty, new SPGPropertyValue(rawValue))

        expect:
        normalizer.propertyLinking(subPropertyRecord)
        subPropertyRecord.getValue().getStds() == expectValues

        where:
        subProperty               | rawValue || expectValues
        getSubProperty("Text")    | 'jack'   || ['jack']
        getSubProperty("Integer") | '100'    || [100]
        getSubProperty("Double")  | '1.1'    || [1.1]
    }


    def getProperty(String oName) {
        return PropertyGenerator.getProperty(
                "aPropertyName", SPGTypeEnum.ENTITY_TYPE,
                'RiskMining.App', SPGTypeEnum.BASIC_TYPE, oName
        )
    }

    def getSubProperty(String oName) {
        return PropertyGenerator.getSubProperty(
                "aSubPropertyName", PropertyGenerator.getProperty(),
                SPGTypeEnum.BASIC_TYPE, oName
        )
    }
}
