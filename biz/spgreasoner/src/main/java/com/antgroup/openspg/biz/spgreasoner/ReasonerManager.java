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

package com.antgroup.openspg.biz.spgreasoner;

import com.antgroup.openspg.api.facade.dto.reasoner.request.ReasonerDslRunRequest;
import com.antgroup.openspg.api.facade.dto.reasoner.request.ReasonerJobInstQuery;
import com.antgroup.openspg.api.facade.dto.reasoner.request.ReasonerJobSubmitRequest;
import com.antgroup.openspg.core.spgreasoner.model.service.JobReasonerReceipt;
import com.antgroup.openspg.core.spgreasoner.model.service.ReasonerJobInst;
import com.antgroup.openspg.core.spgreasoner.model.service.TableReasonerReceipt;

import java.util.List;


public interface ReasonerManager {

    TableReasonerReceipt runDsl(ReasonerDslRunRequest request);

    JobReasonerReceipt submitJob(ReasonerJobSubmitRequest request);

    List<ReasonerJobInst> queryJobInst(ReasonerJobInstQuery query);
}
