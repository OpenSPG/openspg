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

import com.antgroup.openspg.api.facade.dto.builder.request.BuilderJobInstQuery;
import com.antgroup.openspg.api.facade.dto.builder.request.BuilderJobSubmitRequest;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInst;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderReceiptTypeEnum;
import com.antgroup.openspg.core.spgbuilder.model.service.JobBuilderReceipt;
import java.util.List;

public interface BuilderManager {

  /**
   * This method is the entrance of the building service.
   *
   * <p>The front end can call the service to pass in building commands to perform building.
   * Different building receipts will be returned according to different building modes. The front
   * end can execute different follow-up processes according to different building receipts. The
   * type of building receipts can refer to {@link BuilderReceiptTypeEnum}
   *
   * @param request The building commands
   * @return The building receipts
   */
  JobBuilderReceipt submitJobInfo(BuilderJobSubmitRequest request);

  List<BuilderJobInst> queryJobInst(BuilderJobInstQuery query);
}
