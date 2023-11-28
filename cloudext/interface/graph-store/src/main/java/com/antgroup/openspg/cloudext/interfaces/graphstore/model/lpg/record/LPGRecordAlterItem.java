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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents one record alteration in <tt>LPG</tt>, which constants a {@link BaseLPGRecord
 * LPGRecord} and a {@link RecordAlterOperationEnum RecordAlterOperator}.
 */
@Getter
@AllArgsConstructor
public class LPGRecordAlterItem extends BaseValObj {

  private final RecordAlterOperationEnum alterOp;

  private final BaseLPGRecord lpgRecord;
}
