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

package com.antgroup.openspg.server.core.builder.service.repo;

import com.antgroup.openspg.server.api.http.client.dto.builder.request.BuilderJobInstQuery;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInst;
import com.antgroup.openspg.server.core.builder.model.service.BuilderStatusWithProgress;
import java.util.List;

public interface BuilderJobInstRepository {

  Long save(BuilderJobInst jobInst);

  int updateExternalJobId(Long builderJobInstId, String externalJobInstId);

  List<BuilderJobInst> query(BuilderJobInstQuery query);

  int start(Long jobInstId, BuilderStatusWithProgress progress);

  int running(Long jobInstId, BuilderStatusWithProgress progress);

  int finish(Long jobInstId, BuilderStatusWithProgress progress);

  int queue(Long jobInstId, BuilderStatusWithProgress progress);
}
