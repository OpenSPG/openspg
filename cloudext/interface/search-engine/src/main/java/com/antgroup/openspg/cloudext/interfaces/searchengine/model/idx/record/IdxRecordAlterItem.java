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

package com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.core.spgbuilder.model.record.RecordAlterOperationEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class IdxRecordAlterItem extends BaseValObj {

    private final RecordAlterOperationEnum alterOp;

    private final IdxRecord idxRecord;
}
