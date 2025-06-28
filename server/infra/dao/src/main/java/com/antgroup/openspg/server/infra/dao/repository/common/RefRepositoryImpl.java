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

import com.antgroup.openspg.server.common.model.ref.RefInfo;
import com.antgroup.openspg.server.common.model.ref.RefTypeEnum;
import com.antgroup.openspg.server.common.model.ref.RefedTypeEnum;
import com.antgroup.openspg.server.common.service.ref.RefRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.RefDO;
import com.antgroup.openspg.server.infra.dao.mapper.RefDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.RefConvertor;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RefRepositoryImpl implements RefRepository {

  @Autowired private RefDOMapper refDOMapper;

  @Override
  public Long insert(RefInfo refInfo) {
    RefDO refDO = RefConvertor.toDO(refInfo);
    refDO.setGmtCreate(new Date());
    refDO.setGmtModified(new Date());
    refDOMapper.insert(refDO);
    refInfo.setId(refDO.getId());
    return refInfo.getId();
  }

  @Override
  public int deleteById(Long id) {
    return refDOMapper.deleteByPrimaryKey(id);
  }

  @Override
  public int deleteByIds(List<Long> ids) {
    return refDOMapper.deleteByIds(ids);
  }

  @Override
  public int update(RefInfo refInfo) {
    return refDOMapper.updateByPrimaryKeySelective(RefConvertor.toDO(refInfo));
  }

  @Override
  public RefInfo getById(Long id) {
    RefDO refDO = refDOMapper.selectByPrimaryKey(id);
    return RefConvertor.toModel(refDO);
  }

  @Override
  public RefInfo selectByUniqueKey(String refId, String refType, String refedId, String refedType) {
    RefDO refDO = new RefDO();
    refDO.setRefId(refId);
    refDO.setRefType(refType);
    refDO.setRefedId(refedId);
    refDO.setRefedType(refedType);
    RefDO result = refDOMapper.selectByUniqueKey(refDO);
    return RefConvertor.toModel(result);
  }

  @Override
  public int updateByPrimaryKeySelective(RefInfo refInfo) {
    return refDOMapper.updateByPrimaryKeySelective(RefConvertor.toDO(refInfo));
  }

  @Override
  public int updateByUniqueKey(RefInfo refInfo) {
    return refDOMapper.updateByUniqueKey(RefConvertor.toDO(refInfo));
  }

  @Override
  public List<RefInfo> getRefInfoByRef(String refId, RefTypeEnum refType) {
    RefDO refDO = new RefDO();
    List<RefDO> result;
    refDO.setRefId(refId);
    refDO.setRefType(refType.name());
    result = refDOMapper.query(refDO);
    return RefConvertor.toModelList(result);
  }

  @Override
  public List<RefInfo> getRefInfoByRefed(String refedId, RefedTypeEnum refedType) {
    RefDO refDO = new RefDO();
    List<RefDO> result;
    refDO.setRefedId(refedId);
    refDO.setRefedType(refedType.name());
    result = refDOMapper.query(refDO);
    return RefConvertor.toModelList(result);
  }

  @Override
  public List<RefInfo> getRefInfoByRightMatchRefedId(String refedId) {
    return RefConvertor.toModelList(refDOMapper.getRefInfoByRightMatchRefedId(refedId));
  }

  @Override
  public int deleteByUniqueKey(RefInfo refInfo) {
    return refDOMapper.deleteByUniqueKey(RefConvertor.toDO(refInfo));
  }
}
