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

package com.antgroup.openspg.biz.spgschema.impl;

import com.antgroup.openspg.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.biz.spgschema.SchemaManager;
import com.antgroup.openspg.common.model.exception.LockException;
import com.antgroup.openspg.common.model.exception.ProjectException;
import com.antgroup.openspg.common.model.project.Project;
import com.antgroup.openspg.common.service.lock.DistributeLockService;
import com.antgroup.openspg.common.service.project.ProjectService;
import com.antgroup.openspg.core.spgschema.model.SchemaException;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.ProjectSchema;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.service.alter.SchemaAlterPipeline;
import com.antgroup.openspg.core.spgschema.service.alter.model.SchemaAlterContext;
import com.antgroup.openspg.core.spgschema.service.type.SPGTypeService;
import com.antgroup.openspg.core.spgschema.service.type.model.BuiltInPropertyEnum;
import com.antgroup.openspg.core.spgschema.service.util.PropertyUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class SchemaManagerImpl implements SchemaManager {

    private static final String SCHEMA_DEPLOY_LOCK_KEY = "schema_deploy_lock_";
    private static final Long SCHEMA_DEPLOY_LOCK_TIMEOUT = 5 * 60 * 1000L;

    @Autowired
    private SPGTypeService spgTypeService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SchemaAlterPipeline schemaAlterPipeline;
    @Autowired
    private DistributeLockService distributeLockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void alterSchema(SchemaAlterRequest request) {
        Long projectId = request.getProjectId();
        Project project = projectService.queryById(projectId);
        if (null == project) {
            throw ProjectException.projectNotExist(projectId);
        }

        String lockKey = String.format("%s%s", SCHEMA_DEPLOY_LOCK_KEY, projectId.toString());
        boolean locked = distributeLockService.tryLock(lockKey, SCHEMA_DEPLOY_LOCK_TIMEOUT);
        if (!locked) {
            throw LockException.lockFail(lockKey);
        }

        try {
            ProjectSchema projectSchema = spgTypeService.queryProjectSchema(projectId);
            SchemaAlterContext context = new SchemaAlterContext()
                .setProject(project)
                .setReleasedSchema(projectSchema.getSpgTypes())
                .setAlterSchema(request.getSchemaDraft().getAlterSpgTypes());
            schemaAlterPipeline.run(context);
        } catch (Exception e) {
            throw SchemaException.alterError(e);
        } finally {
            distributeLockService.unlock(lockKey);
        }
    }

    @Override
    public ProjectSchema getProjectSchema(Long projectId) {
        Project project = projectService.queryById(projectId);
        if (null == project) {
            throw ProjectException.projectNotExist(projectId);
        }

        return spgTypeService.queryProjectSchema(projectId);
    }

    @Override
    public BaseSPGType getSpgType(String uniqueName) {
        SPGTypeIdentifier spgTypeIdentifier = SPGTypeIdentifier.parse(uniqueName);
        return spgTypeService.querySPGTypeByIdentifier(spgTypeIdentifier);
    }

    @Override
    public List<Property> getBuiltInProperty(SPGTypeEnum spgTypeEnum) {
        List<Property> builtInProperties = new ArrayList<>();
        SPGTypeRef spgTypeRef = new SPGTypeRef(null, spgTypeEnum);

        List<BuiltInPropertyEnum> builtInPropertyEnums =
            BuiltInPropertyEnum.getBuiltInProperty(spgTypeEnum);
        builtInPropertyEnums.forEach(e -> builtInProperties.add(
            PropertyUtils.newProperty(spgTypeRef, e)));
        return builtInProperties;
    }
}
