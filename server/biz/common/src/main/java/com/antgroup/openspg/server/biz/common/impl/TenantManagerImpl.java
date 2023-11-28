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

package com.antgroup.openspg.server.biz.common.impl;

import com.antgroup.openspg.server.biz.common.TenantManager;
import com.antgroup.openspg.server.common.service.tenant.TenantRepository;
import com.antgroup.openspg.server.api.facade.dto.common.request.TenantCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.TenantQueryRequest;
import com.antgroup.openspg.common.model.tenant.Tenant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantManagerImpl implements TenantManager {

  @Autowired private TenantRepository tenantRepository;

  @Override
  public Tenant save(TenantCreateRequest request) {
    Tenant tenant = new Tenant(null, request.getName(), request.getDesc());
    tenantRepository.save(tenant);
    return tenant;
  }

  @Override
  public List<Tenant> query(TenantQueryRequest request) {
    return tenantRepository.query(request);
  }
}
