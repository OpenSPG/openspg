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
package com.antgroup.openspg.server.biz.common;

import com.antgroup.openspg.server.common.model.modeldetail.ModelDetail;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailDTO;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailQuery;
import java.util.List;

public interface ModelDetailManager {

  /** insert model provider */
  Long insert(ModelDetail record);

  /** delete by id */
  int deleteById(Long id);

  /** update model provider */
  Long update(ModelDetail record);

  /** get by id */
  ModelDetail getById(Long id);

  /** query by condition */
  List<ModelDetail> query(ModelDetailQuery record);

  /**
   * query dto by condition
   *
   * @param modelDetailQuery
   * @return
   */
  List<ModelDetailDTO> queryDTO(ModelDetailQuery modelDetailQuery);
}
