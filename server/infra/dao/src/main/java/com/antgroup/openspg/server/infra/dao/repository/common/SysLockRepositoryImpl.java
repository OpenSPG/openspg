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

package com.antgroup.openspg.server.infra.dao.repository.common;

import com.antgroup.openspg.server.infra.dao.dataobject.SysLockDO;
import com.antgroup.openspg.server.infra.dao.dataobject.SysLockDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.SysLockDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.SysLockConvertor;
import com.antgroup.openspg.cloudext.interfaces.repository.sequence.SequenceRepository;
import com.antgroup.openspg.server.common.service.lock.SysLockRepository;
import com.antgroup.openspg.server.common.service.lock.model.SysLock;
import com.antgroup.openspg.common.util.CollectionsUtils;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SysLockRepositoryImpl implements SysLockRepository {

  @Autowired private SequenceRepository sequenceRepository;
  @Autowired private SysLockDOMapper sysLockDOMapper;

  @Override
  public List<SysLock> queryAllLock() {
    SysLockDOExample example = new SysLockDOExample();
    List<SysLockDO> sysLockDOS = sysLockDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(sysLockDOS, SysLockConvertor::toModel);
  }

  @Override
  public boolean exist(String lockName) {
    SysLockDOExample example = new SysLockDOExample();
    example.createCriteria().andMethodNameEqualTo(lockName);
    return sysLockDOMapper.selectByExample(example).size() > 0;
  }

  @Override
  public boolean addLock(String lockName, String lockValue) {
    try {
      SysLockDO sysLockDO = new SysLockDO();
      sysLockDO.setId(sequenceRepository.getSeqIdByTime());
      sysLockDO.setGmtCreate(new Date());
      sysLockDO.setGmtModified(new Date());
      sysLockDO.setMethodName(lockName);
      sysLockDO.setMethodValue(lockValue);
      return sysLockDOMapper.insert(sysLockDO) > 0;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean removeLock(String lockName) {
    SysLockDOExample example = new SysLockDOExample();
    example.createCriteria().andMethodNameEqualTo(lockName);
    return sysLockDOMapper.deleteByExample(example) > 0;
  }
}
