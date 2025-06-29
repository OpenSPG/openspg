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
package com.antgroup.openspg.server.biz.common.impl;

import com.antgroup.openspg.server.biz.common.ModelDetailManager;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetail;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailDTO;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailQuery;
import com.antgroup.openspg.server.common.service.modeldetail.ModelDetailRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModelDetailManagerImpl implements ModelDetailManager {

  @Autowired private ModelDetailRepository modelDetailRepository;

  @Override
  public Long insert(ModelDetail record) {
    return modelDetailRepository.insert(record);
  }

  @Override
  public int deleteById(Long id) {
    return modelDetailRepository.deleteById(id);
  }

  @Override
  public Long update(ModelDetail record) {
    return modelDetailRepository.update(record);
  }

  @Override
  public ModelDetail getById(Long id) {
    return modelDetailRepository.getById(id);
  }

  @Override
  public List<ModelDetail> query(ModelDetailQuery record) {
    return modelDetailRepository.query(record);
  }

  @Override
  public List<ModelDetailDTO> queryDTO(ModelDetailQuery modelDetailQuery) {
    return modelDetailRepository.queryDTO(modelDetailQuery);
  }
}
