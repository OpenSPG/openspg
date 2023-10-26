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

package com.antgroup.openspg.core.spgbuilder.model.record;

import com.antgroup.openspg.common.model.base.BaseValObj;


public class SPGRecordAlterItem extends BaseValObj {

    private final RecordAlterOperationEnum alterOp;

    private final BaseSPGRecord spgRecord;

    public SPGRecordAlterItem(RecordAlterOperationEnum alterOp, BaseSPGRecord spgRecord) {
        this.alterOp = alterOp;
        this.spgRecord = spgRecord;
    }

    public RecordAlterOperationEnum getAlterOp() {
        return alterOp;
    }

    public BaseSPGRecord getSpgRecord() {
        return spgRecord;
    }
}
