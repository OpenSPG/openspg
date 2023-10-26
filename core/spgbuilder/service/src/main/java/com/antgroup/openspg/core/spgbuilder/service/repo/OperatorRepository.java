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

package com.antgroup.openspg.core.spgbuilder.service.repo;

import com.antgroup.openspg.core.spgbuilder.model.operator.OperatorOverview;
import com.antgroup.openspg.core.spgbuilder.model.operator.OperatorVersion;

import javax.annotation.Nullable;

import java.util.List;


public interface OperatorRepository {

    OperatorOverview query(Long overviewId);

    List<OperatorOverview> query(@Nullable String name);

    List<OperatorOverview> batchQuery(List<String> names);

    List<OperatorVersion> batchQuery(Long overviewId, List<Integer> versions);

    int save(OperatorOverview operatorOverview);

    List<OperatorVersion> list(String name);

    List<OperatorVersion> list(Long overviewId);

    int save(OperatorVersion operatorVersion);
}
