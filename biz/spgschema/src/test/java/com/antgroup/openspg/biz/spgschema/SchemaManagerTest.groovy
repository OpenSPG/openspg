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

package com.antgroup.openspg.biz.spgschema

import com.antgroup.openspg.api.facade.dto.schema.request.SchemaAlterRequest
import com.antgroup.openspg.biz.spgschema.impl.SchemaManagerImpl
import com.antgroup.openspg.common.model.exception.LockException
import com.antgroup.openspg.common.model.exception.ProjectException
import com.antgroup.openspg.common.model.project.Project
import com.antgroup.openspg.common.service.lock.DistributeLockService
import com.antgroup.openspg.common.service.project.ProjectService
import com.antgroup.openspg.core.spgschema.model.alter.SchemaDraft
import com.antgroup.openspg.core.spgschema.model.type.BaseAdvancedType
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType
import com.antgroup.openspg.core.spgschema.model.type.ProjectSchema
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum
import com.antgroup.openspg.core.spgschema.service.alter.SchemaAlterPipeline
import com.antgroup.openspg.core.spgschema.service.alter.model.SchemaAlterContext
import com.antgroup.openspg.core.spgschema.service.type.SPGTypeService
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertEquals

class SchemaManagerTest extends Specification {
    def spgTypeService = Mock(SPGTypeService.class)
    def projectService = Mock(ProjectService.class)
    def schemaAlterPipeline = Mock(SchemaAlterPipeline.class)
    def distributeLockService = Mock(DistributeLockService.class)

    def schemaManager = new SchemaManagerImpl(
            spgTypeService: spgTypeService,
            projectService: projectService,
            schemaAlterPipeline: schemaAlterPipeline,
            distributeLockService: distributeLockService)

    def "test alterSchema"() {
        given:
        SchemaAlterRequest request = new SchemaAlterRequest()
        request.setProjectId(1L)
        request.setSchemaDraft(new SchemaDraft())

        projectService.queryById(_) >> project
        distributeLockService.tryLock(_, _) >> lock
        spgTypeService.queryProjectSchema(_) >> new ProjectSchema(new ArrayList<BaseSPGType>())

        SchemaAlterContext context = new SchemaAlterContext()
                .setProject(project)
                .setAlterSchema(new ArrayList<BaseAdvancedType>())
                .setReleasedSchema(new ArrayList<BaseSPGType>())
        schemaAlterPipeline.run(context)

        when:
        schemaManager.alterSchema(request)

        then:
        thrown(exp)

        where:
        project                                    | lock  || exp
        null                                       | true  || ProjectException.class
        new Project(1L, null, null, "DEFAULT", 1L) | false || LockException
    }

    def "test alterSchema2"() {
        given:
        SchemaAlterRequest request = new SchemaAlterRequest()
        request.setProjectId(1L)
        request.setSchemaDraft(new SchemaDraft())

        projectService.queryById(_) >> project
        distributeLockService.tryLock(_, _) >> lock
        spgTypeService.queryProjectSchema(_) >> new ProjectSchema(new ArrayList<BaseSPGType>())

        SchemaAlterContext context = new SchemaAlterContext()
                .setProject(project)
                .setAlterSchema(new ArrayList<BaseAdvancedType>())
                .setReleasedSchema(new ArrayList<BaseSPGType>())
        schemaAlterPipeline.run(context)

        when:
        def v = schemaManager.alterSchema(request)

        then:
        v == ret

        where:
        project                                    | lock || ret
        new Project(1L, null, null, "DEFAULT", 1L) | true || null
    }

    def "test getBuiltInProperty"() {
        given:

        when:
        def p = schemaManager.getBuiltInProperty(SPGTypeEnum.CONCEPT_TYPE)

        then:
        assertEquals(2, p.size())
    }
}
