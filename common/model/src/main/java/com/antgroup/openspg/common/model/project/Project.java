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

package com.antgroup.openspg.common.model.project;

import com.antgroup.openspg.common.model.base.BaseModel;

/**
 * Namespace unit for department manager self schema, the schema elements such as entityType or property between Project
 * is isolated.
 */
public class Project extends BaseModel {

    private static final long serialVersionUID = -3046737313733029469L;

    /**
     * Unique id
     */
    private Long id;

    /**
     * English name
     */
    private final String name;

    /**
     * Detail description
     */
    private final String description;

    /**
     * The namespace that isolate entity from different project.
     */
    private final String namespace;

    /**
     * The tenant id that project belong to.
     */
    private final Long tenantId;

    public Project(
        Long id, String name,
        String description,
        String namespace, Long tenantId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.namespace = namespace;
        this.tenantId = tenantId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getNamespace() {
        return namespace;
    }
}
