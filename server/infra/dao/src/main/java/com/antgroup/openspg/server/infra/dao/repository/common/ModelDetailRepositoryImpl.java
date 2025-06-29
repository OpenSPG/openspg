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
package com.antgroup.openspg.server.infra.dao.repository.common;

import com.antgroup.openspg.server.common.model.modeldetail.ModelDetail;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailDTO;
import com.antgroup.openspg.server.common.model.modeldetail.ModelDetailQuery;
import com.antgroup.openspg.server.common.service.modeldetail.ModelDetailRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.ModelDetailDO;
import com.antgroup.openspg.server.infra.dao.mapper.ModelDetailDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.ModelDetailConvertor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ModelDetailRepositoryImpl implements ModelDetailRepository {

  @Autowired private ModelDetailDOMapper modelDetailDOMapper;

  @Override
  public Long insert(ModelDetail record) {
    ModelDetailDO modelDetailDO = ModelDetailConvertor.toDO(record);
    modelDetailDOMapper.insert(modelDetailDO);
    record.setId(modelDetailDO.getId());
    return record.getId();
  }

  @Override
  public int deleteById(Long id) {
    return modelDetailDOMapper.deleteById(id);
  }

  @Override
  public Long update(ModelDetail record) {
    return modelDetailDOMapper.update(ModelDetailConvertor.toDO(record));
  }

  @Override
  public ModelDetail getById(Long id) {
    return ModelDetailConvertor.toModel(modelDetailDOMapper.getById(id));
  }

  @Override
  public List<ModelDetail> query(ModelDetailQuery record) {
    return ModelDetailConvertor.toModelList(modelDetailDOMapper.query(record));
  }

  @Override
  public List<ModelDetailDTO> queryDTO(ModelDetailQuery record) {
    return ModelDetailConvertor.toDTOList(modelDetailDOMapper.query(record));
  }
}
