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

package com.antgroup.openspg.cloudext.interfaces.computing.cmd;

import com.antgroup.openspg.builder.model.pipeline.config.GraphStoreSinkNodeConfig;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.common.model.base.BaseCmd;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInfo;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInst;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BuilderJobSubmitCmd extends BaseCmd {

  private final BuilderJobInst jobInst;

  private final BuilderJobInfo jobInfo;

  private final GraphStoreSinkNodeConfig sinkNodeConfig;

  private final String schemaUrl;

  private final ProjectSchema projectSchema;

  private final Map<String, Object> params;
}
