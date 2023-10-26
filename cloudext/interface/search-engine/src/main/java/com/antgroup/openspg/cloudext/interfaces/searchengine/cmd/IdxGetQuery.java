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

package com.antgroup.openspg.cloudext.interfaces.searchengine.cmd;

import com.antgroup.openspg.common.model.base.BaseQuery;

import lombok.Getter;

import java.util.Set;


@Getter
public class IdxGetQuery extends BaseQuery {

    private final String idxName;

    private final Set<String> docIds;

    public IdxGetQuery(String idxName, Set<String> docIds) {
        this.idxName = idxName;
        this.docIds = docIds;
    }
}
