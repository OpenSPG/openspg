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
import com.antgroup.openspg.server.common.model.retrieval.Retrieval;
import com.antgroup.openspg.server.common.model.retrieval.RetrievalQuery;
import com.antgroup.openspg.server.common.service.retrieval.RetrievalRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.RetrievalDO;
import com.antgroup.openspg.server.infra.dao.mapper.RetrievalDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.RetrievalConvertor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RetrievalRepositoryImpl implements RetrievalRepository {

  @Autowired private RetrievalDOMapper retrievalDOMapper;

  @Override
  public Long insert(Retrieval record) {
    RetrievalDO retrievalDO = RetrievalConvertor.toDO(record);
    retrievalDOMapper.insert(retrievalDO);
    record.setId(retrievalDO.getId());
    return retrievalDO.getId();
  }

  @Override
  public int deleteById(Long id) {
    return retrievalDOMapper.deleteById(id);
  }

  @Override
  public Long update(Retrieval record) {
    return retrievalDOMapper.update(RetrievalConvertor.toDO(record));
  }

  @Override
  public Retrieval getById(Long id) {
    return RetrievalConvertor.toModel(retrievalDOMapper.getById(id));
  }

  @Override
  public Retrieval getByName(String name) {
    return RetrievalConvertor.toModel(retrievalDOMapper.getByName(name));
  }

  @Override
  public Paged<Retrieval> query(RetrievalQuery record) {
    Paged<Retrieval> pageData = new Paged(record.getPageSize(), record.getPageNo());
    int count = retrievalDOMapper.selectCountByQuery(record);
    pageData.setTotal(Long.valueOf(count));
    if (count <= 0) {
      pageData.setResults(Lists.newArrayList());
      return pageData;
    }
    CommonUtils.checkQueryPage(count, record.getPageNo(), record.getPageSize());
    pageData.setResults(RetrievalConvertor.toModelList(retrievalDOMapper.query(record)));
    return pageData;
  }
}
