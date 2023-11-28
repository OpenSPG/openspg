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

package com.antgroup.openspg.server.biz.builder;

import com.antgroup.openspg.builder.core.operator.OperatorOverview;
import com.antgroup.openspg.builder.core.operator.OperatorVersion;
import com.antgroup.openspg.server.api.http.client.dto.builder.request.OperatorCreateRequest;
import com.antgroup.openspg.server.api.http.client.dto.builder.request.OperatorVersionRequest;
import com.antgroup.openspg.server.api.http.client.dto.builder.response.OperatorCreateResponse;
import com.antgroup.openspg.server.api.http.client.dto.builder.response.OperatorVersionResponse;
import java.io.InputStream;
import java.util.List;

public interface OperatorManager {

  OperatorCreateResponse create(OperatorCreateRequest request);

  OperatorVersionResponse addVersion(OperatorVersionRequest request, InputStream file);

  List<OperatorOverview> listOverview(String name);

  List<OperatorVersion> listVersion(String name);
}
