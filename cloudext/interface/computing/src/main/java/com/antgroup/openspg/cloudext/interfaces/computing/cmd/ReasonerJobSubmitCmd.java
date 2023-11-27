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

import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInfo;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInst;
import com.antgroup.openspg.server.common.model.base.BaseCmd;
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.server.common.model.datasource.connection.TableStoreConnectionInfo;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReasonerJobSubmitCmd extends BaseCmd {

  private final ReasonerJobInst jobInst;

  private final ReasonerJobInfo jobInfo;

  private final GraphStoreConnectionInfo graphStoreConnInfo;

  private final TableStoreConnectionInfo tableStoreConnInfo;

  private final String schemaUrl;

  private final Map<String, Object> params;

  public String tableName() {
    return String.format("spgreasoner_%s_%s_result", jobInfo.getJobName(), jobInst.getJobInstId());
  }
}
