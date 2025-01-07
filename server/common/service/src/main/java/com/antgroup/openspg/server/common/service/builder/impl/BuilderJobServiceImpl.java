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
package com.antgroup.openspg.server.common.service.builder.impl;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.bulider.BuilderJobQuery;
import com.antgroup.openspg.server.common.service.builder.BuilderJobRepository;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BuilderJobServiceImpl implements BuilderJobService {

  @Autowired private BuilderJobRepository builderJobRepository;

  @Override
  public Long insert(BuilderJob record) {
    return builderJobRepository.insert(record);
  }

  @Override
  public int deleteById(Long id) {
    return builderJobRepository.deleteById(id);
  }

  @Override
  public Long update(BuilderJob record) {
    return builderJobRepository.update(record);
  }

  @Override
  public BuilderJob getById(Long id) {
    return builderJobRepository.getById(id);
  }

  @Override
  public Paged<BuilderJob> query(BuilderJobQuery record) {
    return builderJobRepository.query(record);
  }
}
