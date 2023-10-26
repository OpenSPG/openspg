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

package com.antgroup.openspg.core.spgreasoner.model.service;


public class JobReasonerReceipt extends BaseReasonerReceipt {

    private final Long reasonerJobInfoId;

    private final Long reasonerJobInstId;

    public JobReasonerReceipt(Long reasonerJobInfoId, Long reasonerJobInstId) {
        super(ReasonerReceiptTypeEnum.JOB);
        this.reasonerJobInfoId = reasonerJobInfoId;
        this.reasonerJobInstId = reasonerJobInstId;
    }

    public Long getReasonerJobInfoId() {
        return reasonerJobInfoId;
    }

    public Long getReasonerJobInstId() {
        return reasonerJobInstId;
    }
}
