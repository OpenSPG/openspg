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

package com.antgroup.openspg.server.core.builder.model.service;

import com.antgroup.openspg.server.common.model.base.BaseToString;

/**
 * Base class for building receipts.
 *
 * <p>Contains the building mode and the building receipt type for the current building instance
 */
public abstract class BaseBuilderReceipt extends BaseToString {

  /** the building receipt type */
  private final BuilderReceiptTypeEnum receiptType;

  public BaseBuilderReceipt(BuilderReceiptTypeEnum receiptType) {
    this.receiptType = receiptType;
  }

  public BuilderReceiptTypeEnum getReceiptType() {
    return receiptType;
  }
}
