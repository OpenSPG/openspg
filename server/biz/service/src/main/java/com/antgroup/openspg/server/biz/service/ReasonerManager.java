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
package com.antgroup.openspg.server.biz.service;

import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.api.facade.dto.service.request.ReasonerTaskRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.ThinkerTaskRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.ReasonerTaskResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.ThinkerTaskResponse;

public interface ReasonerManager {
  ThinkerTaskResponse thinker(ThinkerTaskRequest request);

  ReasonerTaskResponse reason(ReasonerTaskRequest request);

  ProjectSchema getReasonSchema(Long projectId);
}
