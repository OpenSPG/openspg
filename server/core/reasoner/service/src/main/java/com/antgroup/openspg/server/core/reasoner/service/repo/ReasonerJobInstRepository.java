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

package com.antgroup.openspg.server.core.reasoner.service.repo;

import com.antgroup.openspg.api.facade.dto.reasoner.request.ReasonerJobInstQuery;
import com.antgroup.openspg.core.spgreasoner.model.service.FailureReasonerResult;
import com.antgroup.openspg.core.spgreasoner.model.service.ReasonerJobInst;
import com.antgroup.openspg.core.spgreasoner.model.service.ReasonerStatusWithProgress;
import java.util.List;

public interface ReasonerJobInstRepository {

  Long save(ReasonerJobInst jobInst);

  int updateExternalJobId(Long reasonerJobInstId, String externalJobInstId);

  List<ReasonerJobInst> query(ReasonerJobInstQuery query);

  int updateStatus(Long jobInstId, ReasonerStatusWithProgress process);

  int updateToFailure(Long jobInstId, FailureReasonerResult result);
}
