/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.server.api.facade.dto.common.request;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import java.util.Map;
import lombok.Data;

@Data
public class KagBuilderRequest extends BaseRequest {

  private Long projectId;

  private String userNumber;

  private String command;

  private String image;

  private String workerPool;

  private Integer workerNum;

  private Double workerCpu;

  private Integer workerGpu;

  private String workerGpuType;

  private Integer workerMemory;

  private Integer workerStorage;

  private Map<String, String> envs;
}
