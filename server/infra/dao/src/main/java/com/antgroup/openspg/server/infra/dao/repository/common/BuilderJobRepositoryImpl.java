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

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.bulider.BuilderJobQuery;
import com.antgroup.openspg.server.common.service.builder.BuilderJobRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.BuilderJobDO;
import com.antgroup.openspg.server.infra.dao.mapper.BuilderJobDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.BuilderJobConvertor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BuilderJobRepositoryImpl implements BuilderJobRepository {

  @Autowired private BuilderJobDOMapper builderJobDOMapper;

  @Override
  public Long insert(BuilderJob record) {
    BuilderJobDO jobDO = BuilderJobConvertor.toDO(record);
    builderJobDOMapper.insert(jobDO);
    record.setId(jobDO.getId());
    return jobDO.getId();
  }

  @Override
  public int deleteById(Long id) {
    return builderJobDOMapper.deleteById(id);
  }

  @Override
  public Long update(BuilderJob record) {
    return builderJobDOMapper.update(BuilderJobConvertor.toDO(record));
  }

  @Override
  public BuilderJob getById(Long id) {
    return BuilderJobConvertor.toModel(builderJobDOMapper.getById(id));
  }

  @Override
  public Paged<BuilderJob> query(BuilderJobQuery record) {
    Paged<BuilderJob> pageData = new Paged(record.getPageSize(), record.getPageNo());
    int count = builderJobDOMapper.selectCountByQuery(record);
    pageData.setTotal(Long.valueOf(count));
    if (count <= 0) {
      pageData.setResults(Lists.newArrayList());
      return pageData;
    }
    CommonUtils.checkQueryPage(count, record.getPageNo(), record.getPageSize());
    pageData.setResults(BuilderJobConvertor.toModelList(builderJobDOMapper.query(record)));
    return pageData;
  }
}
