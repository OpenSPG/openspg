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

package com.antgroup.openspg.cloudext.interfaces.graphstore;

import com.antgroup.openspg.common.util.cloudext.CloudExtException;

public class GraphStoreException extends CloudExtException {

  private GraphStoreException(Throwable cause, String messagePattern, Object... args) {
    super(cause, messagePattern, args);
  }

  private GraphStoreException(String messagePattern, Object... args) {
    this(null, messagePattern, args);
  }

  public static GraphStoreException unexpectedAlterOperationEnum(Object operationEnum) {
    return new GraphStoreException(
        "unexpected alter operation enum {}", String.valueOf(operationEnum));
  }

  public static GraphStoreException unexpectedVertexEdgeTypeOperationEnum(Object operationEnum) {
    return new GraphStoreException(
        "unexpected vertex edge type operation enum {}", String.valueOf(operationEnum));
  }

  public static GraphStoreException unexpectedSPGRecordTypeEnum(Object recordTypeEnum) {
    return new GraphStoreException(
        "unexpected spg record type enum {}", String.valueOf(recordTypeEnum));
  }

  public static GraphStoreException unexpectedSPGRecordType(Object spgRecord) {
    return new GraphStoreException("unexpected spg record type {}", String.valueOf(spgRecord));
  }

  public static GraphStoreException unexpectedSPGPropertyRecordType(Object propertyRecord) {
    return new GraphStoreException(
        "unexpected spg property record type {}", String.valueOf(propertyRecord));
  }

  public static GraphStoreException unexpectedSPGTypeEnum(Object spgTypeEnum) {
    return new GraphStoreException("unexpected spg type enum {}", String.valueOf(spgTypeEnum));
  }

  public static GraphStoreException unexpectedLPGRecordTypeEnum(Object lpgRecordTypeEnum) {
    return new GraphStoreException(
        "unexpected lpg record type enum {}", String.valueOf(lpgRecordTypeEnum));
  }
}
