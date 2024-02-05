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

import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.server.api.facade.dto.common.request.TenantQueryRequest;
import com.antgroup.openspg.server.common.model.tenant.Tenant;
import com.antgroup.openspg.server.common.service.tenant.TenantRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.TenantDO;
import com.antgroup.openspg.server.infra.dao.dataobject.TenantDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.TenantDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.TenantConvertor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TenantRepositoryImpl implements TenantRepository {

  @Autowired private TenantDOMapper tenantDOMapper;

  @Override
  public int save(Tenant tenant) {
    TenantDO tenantDO = TenantConvertor.toDO(tenant);
    return tenantDOMapper.insert(tenantDO);
  }

  @Override
  public List<Tenant> query(TenantQueryRequest request) {
    TenantDOExample example = new TenantDOExample();

    TenantDOExample.Criteria criteria = example.createCriteria();
    if (request.getTenantId() != null) {
      criteria.andIdEqualTo(request.getTenantId());
    }

    List<TenantDO> tenantDOS = tenantDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(tenantDOS, TenantConvertor::toModel);
  }
}
