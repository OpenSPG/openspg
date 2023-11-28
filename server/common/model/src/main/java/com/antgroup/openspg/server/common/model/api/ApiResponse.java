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

package com.antgroup.openspg.server.common.model.api;

import com.antgroup.openspg.server.common.model.base.BaseToString;

/**
 * API response returned by API call.
 *
 * @param <T> The type of data that is deserialized from response body
 */
public class ApiResponse<T> extends BaseToString {

  private T data;
  private boolean success;
  private String errorMsg;
  private String remote;
  private String traceId;

  public T getData() {
    if (success) {
      return data;
    } else {
      throw ApiException.withErrorMsg(errorMsg);
    }
  }

  public T getDataThrowsIfNull(String name) {
    T result = getData();
    if (result == null) {
      throw ApiException.notFound(name);
    }
    return result;
  }

  public void setData(T data) {
    this.data = data;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public String getRemote() {
    return remote;
  }

  public void setRemote(String remote) {
    this.remote = remote;
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }

  public static <T> ApiResponse<T> success(T data) {
    ApiResponse<T> apiResponse = new ApiResponse<>();
    apiResponse.setSuccess(true);
    apiResponse.setData(data);
    return apiResponse;
  }

  public static <T> ApiResponse<T> failure(String errorMsg) {
    ApiResponse<T> apiResponse = new ApiResponse<>();
    apiResponse.setSuccess(false);
    apiResponse.setErrorMsg(errorMsg);
    return apiResponse;
  }
}
