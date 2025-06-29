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
package com.antgroup.openspg.server.infra.dao.mapper;

import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailQuery;
import com.antgroup.openspg.server.infra.dao.dataobject.ModelDetailDO;
import java.util.List;

public interface ModelDetailDOMapper {

  Long insert(ModelDetailDO modelDetailDO);

  int deleteById(Long id);

  Long update(ModelDetailDO modelDetailDO);

  ModelDetailDO getById(Long id);

  List<ModelDetailDO> query(ModelDetailQuery modelDetailQuery);
}
