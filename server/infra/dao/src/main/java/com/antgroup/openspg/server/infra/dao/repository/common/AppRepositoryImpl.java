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

import com.antgroup.openspg.server.api.facade.dto.common.request.AppRequest;
import com.antgroup.openspg.server.common.model.app.App;
import com.antgroup.openspg.server.common.service.app.AppRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.AppDO;
import com.antgroup.openspg.server.infra.dao.mapper.AppMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.AppConvertor;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AppRepositoryImpl implements AppRepository {

  @Autowired private AppMapper appMapper;

  @Override
  public Integer save(App app) {
    if (app == null) {
      return 0;
    }
    AppDO appDO = AppConvertor.toDO(app);
    int insert = appMapper.insert(appDO);
    app.setId(appDO.getId());
    return insert;
  }

  @Override
  public Integer update(App app) {
    if (app == null) {
      return 0;
    }
    return appMapper.update(AppConvertor.toDO(app));
  }

  @Override
  public App queryById(Long id) {
    if (id == null) {
      return null;
    }
    return AppConvertor.toModel(appMapper.getById(id));
  }

  @Override
  public List<App> queryPage(AppRequest request, int start, int size) {
    int startIndex = (Math.max(start, 1) - 1) * size;
    return AppConvertor.toModelList(appMapper.selectByCondition(request, startIndex, size));
  }

  @Override
  public Long selectCountByCondition(AppRequest request) {
    return appMapper.selectCountByCondition(request);
  }

  @Override
  public Integer deleteById(Long id) {
    if (id == null) {
      return 0;
    }
    return appMapper.deleteById(id);
  }

  @Override
  public App queryByName(String name) {
    return AppConvertor.toModel(appMapper.getByName(name));
  }

  @Override
  public List<App> queryByCondition(AppRequest request) {
    List<AppDO> appDOList = appMapper.queryByCondition(request);
    if (CollectionUtils.isEmpty(appDOList)) {
      return Collections.emptyList();
    }
    return appDOList.stream().map(AppConvertor::toModel).collect(Collectors.toList());
  }
}
