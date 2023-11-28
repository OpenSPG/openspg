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

package com.antgroup.openspg.schema.http.client;

import com.antgroup.openspg.common.model.api.ApiConstants;
import com.antgroup.openspg.common.model.api.ApiException;
import com.antgroup.openspg.common.model.api.ApiResponse;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestResponse;
import java.util.function.Function;

public class ForestUtils {

  public static <T, C> ApiResponse<T> call(Class<C> clazz, Function<C, ForestResponse<T>> func) {
    return call(
        clazz,
        func,
        forestResponse -> {
          ApiResponse<T> apiResponse = null;
          if (forestResponse.isSuccess()) {
            apiResponse = ApiResponse.success(forestResponse.getResult());
          } else if (forestResponse.getException() != null) {
            throw ApiException.connectError(forestResponse.getException());
          } else {
            if (forestResponse.getStatusCode() == 404) {
              // If the response is 404, we return success status but data is null
              apiResponse = ApiResponse.success(null);
            } else {
              // Otherwise we return a failure status with an error message
              apiResponse = ApiResponse.failure(forestResponse.getContent());
            }
          }
          return apiResponse;
        });
  }

  public static <T, C> ApiResponse<T> call(
      Class<C> clazz,
      Function<C, ForestResponse<T>> func1,
      Function<ForestResponse<T>, ApiResponse<T>> func2) {
    C client = Forest.client(clazz);
    ForestResponse<T> forestResponse = func1.apply(client);
    String remote = forestResponse.getHeaderValue(ApiConstants.REMOTE);
    String traceId = forestResponse.getHeaderValue(ApiConstants.TRACE_ID);

    ApiResponse<T> apiResponse = func2.apply(forestResponse);
    apiResponse.setTraceId(traceId);
    apiResponse.setRemote(remote);
    return apiResponse;
  }
}
