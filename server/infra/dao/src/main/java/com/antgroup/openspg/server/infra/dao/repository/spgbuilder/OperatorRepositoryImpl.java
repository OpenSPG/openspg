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

package com.antgroup.openspg.server.infra.dao.repository.spgbuilder;

import com.antgroup.openspg.server.infra.dao.dataobject.OperatorOverviewDO;
import com.antgroup.openspg.server.infra.dao.dataobject.OperatorOverviewDOExample;
import com.antgroup.openspg.server.infra.dao.dataobject.OperatorVersionDO;
import com.antgroup.openspg.server.infra.dao.dataobject.OperatorVersionDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.OperatorOverviewDOMapper;
import com.antgroup.openspg.server.infra.dao.mapper.OperatorVersionDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.spgbuilder.convertor.OperatorConvertor;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.core.spgbuilder.service.repo.OperatorRepository;
import com.antgroup.openspg.builder.core.operator.OperatorOverview;
import com.antgroup.openspg.builder.core.operator.OperatorVersion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OperatorRepositoryImpl implements OperatorRepository {

  @Autowired private OperatorOverviewDOMapper operatorOverviewDOMapper;

  @Autowired private OperatorVersionDOMapper operatorVersionDOMapper;

  @Override
  public OperatorOverview query(Long overviewId) {
    OperatorOverviewDOExample example = new OperatorOverviewDOExample();
    OperatorOverviewDOExample.Criteria criteria = example.createCriteria();
    if (overviewId != null) {
      criteria.andIdEqualTo(overviewId);
    }
    List<OperatorOverviewDO> operatorOverviewDOS =
        operatorOverviewDOMapper.selectByExample(example);
    if (CollectionUtils.isEmpty(operatorOverviewDOS)) {
      return null;
    }
    return OperatorConvertor.toModel(operatorOverviewDOS.get(0));
  }

  @Override
  public List<OperatorOverview> query(String name) {
    OperatorOverviewDOExample example = new OperatorOverviewDOExample();
    OperatorOverviewDOExample.Criteria criteria = example.createCriteria();
    if (name != null) {
      criteria.andNameEqualTo(name);
    }
    List<OperatorOverviewDO> operatorOverviewDOS =
        operatorOverviewDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(operatorOverviewDOS, OperatorConvertor::toModel);
  }

  @Override
  public List<OperatorOverview> batchQuery(List<String> names) {
    OperatorOverviewDOExample example = new OperatorOverviewDOExample();
    example.createCriteria().andNameIn(names);

    List<OperatorOverviewDO> operatorOverviewDOS =
        operatorOverviewDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(operatorOverviewDOS, OperatorConvertor::toModel);
  }

  @Override
  public List<OperatorVersion> batchQuery(Long overviewId, List<Integer> versions) {
    if (CollectionUtils.isEmpty(versions)) {
      return Collections.emptyList();
    }
    OperatorVersionDOExample example = new OperatorVersionDOExample();
    example.createCriteria().andOverviewIdEqualTo(overviewId).andVersionIn(versions);

    example.setOrderByClause("version desc");
    List<OperatorVersionDO> operatorVersionDOS = operatorVersionDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(operatorVersionDOS, OperatorConvertor::toModel);
  }

  @Override
  public int save(OperatorOverview operatorOverview) {
    OperatorOverviewDO operatorOverviewDO = OperatorConvertor.toDO(operatorOverview);
    return operatorOverviewDOMapper.insert(operatorOverviewDO);
  }

  @Override
  public List<OperatorVersion> list(String name) {
    List<OperatorOverview> operatorOverviews = query(name);
    if (CollectionUtils.isEmpty(operatorOverviews)) {
      return new ArrayList<>(0);
    }
    return list(operatorOverviews.get(0).getId());
  }

  @Override
  public List<OperatorVersion> list(Long overviewId) {
    OperatorVersionDOExample example = new OperatorVersionDOExample();
    example.createCriteria().andOverviewIdEqualTo(overviewId);

    example.setOrderByClause("version desc");
    List<OperatorVersionDO> operatorVersionDOS = operatorVersionDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(operatorVersionDOS, OperatorConvertor::toModel);
  }

  @Override
  public int save(OperatorVersion operatorVersion) {
    OperatorVersionDO operatorVersionDO = OperatorConvertor.toDO(operatorVersion);
    return operatorVersionDOMapper.insert(operatorVersionDO);
  }
}
