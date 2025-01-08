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
import lombok.Data;

@Data
public class ConfigRequest extends BaseRequest {

  private Long id;

  private String configName;

  private String configId;

  private String version;

  private String config;
}
