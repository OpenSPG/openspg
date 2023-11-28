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

package com.antgroup.openspg.server.core.reasoner.model.service;

import com.antgroup.openspg.server.common.model.base.BaseToString;

/**
 * Base class for reasoning receipts.
 *
 * <p>Contains the reasoning mode and the reasoning receipt type for the current reasoning instance
 */
public abstract class BaseReasonerReceipt extends BaseToString {

  /** the reasoning receipt type */
  private final ReasonerReceiptTypeEnum receiptType;

  public BaseReasonerReceipt(ReasonerReceiptTypeEnum receiptType) {
    this.receiptType = receiptType;
  }

  public ReasonerReceiptTypeEnum getReceiptType() {
    return receiptType;
  }
}
