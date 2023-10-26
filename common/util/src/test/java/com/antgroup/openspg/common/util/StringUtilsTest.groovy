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


package com.antgroup.openspg.common.util


import spock.lang.Specification

/**
 *  */
class StringUtilsTest extends Specification {

    static def dict1 = ['labelType': 'vertex', 'labelName': 'app']
    static def template1 = '''CALL db.createLabel('${labelType}', '${labelName}', '${extra}'${fieldSpec})'''
    static def result1 = '''CALL db.createLabel('vertex', 'app', '${extra}'${fieldSpec})'''

    static def dict2 = ['a': 'vertex', 'b': 'app']
    static def template2 = '''CALL db.createLabel('${labelType}', '${labelName}', '${extra}'${fieldSpec})'''
    static def result2 = '''CALL db.createLabel('${labelType}', '${labelName}', '${extra}'${fieldSpec})'''

    def "testDictFormat"() {
        expect:
        result == StringUtils.dictFormat(dict, template)

        where:
        dict  | template  || result
        dict1 | template1 || result1
        dict2 | template2 || result2
    }
}
