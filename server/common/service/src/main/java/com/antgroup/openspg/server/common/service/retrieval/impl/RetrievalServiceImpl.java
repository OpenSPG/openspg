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
package com.antgroup.openspg.server.common.service.retrieval.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.bulider.BuilderJobQuery;
import com.antgroup.openspg.server.common.model.retrieval.Retrieval;
import com.antgroup.openspg.server.common.model.retrieval.RetrievalQuery;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.retrieval.RetrievalRepository;
import com.antgroup.openspg.server.common.service.retrieval.RetrievalService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RetrievalServiceImpl implements RetrievalService {

  @Autowired private RetrievalRepository retrievalRepository;

  @Autowired private BuilderJobService builderJobService;

  @Override
  public Long insert(Retrieval record) {
    return retrievalRepository.insert(record);
  }

  @Override
  public int deleteById(Long id) {
    return retrievalRepository.deleteById(id);
  }

  @Override
  public Long update(Retrieval record) {
    return retrievalRepository.update(record);
  }

  @Override
  public Retrieval getById(Long id) {
    return retrievalRepository.getById(id);
  }

  @Override
  public Retrieval getByName(String name) {
    return retrievalRepository.getByName(name);
  }

  @Override
  public Paged<Retrieval> query(RetrievalQuery record) {
    return retrievalRepository.query(record);
  }

  @Override
  public List<Retrieval> getRetrievalByProjectId(Long projectId) {
    BuilderJobQuery query = new BuilderJobQuery();
    query.setProjectId(projectId);
    List<BuilderJob> jobs = builderJobService.query(query).getResults();
    Set<Long> ids = Sets.newHashSet();
    jobs.forEach(
        job -> {
          if (StringUtils.isNotBlank(job.getRetrievals())) {
            List<Long> retrievals =
                JSON.parseObject(job.getRetrievals(), new TypeReference<List<Long>>() {});
            ids.addAll(retrievals);
          }
        });
    if (CollectionUtils.isEmpty(ids)) {
      return Lists.newArrayList();
    }
    RetrievalQuery record = new RetrievalQuery();
    record.setIds(ids.stream().collect(Collectors.toList()));
    return this.query(record).getResults();
  }
}
