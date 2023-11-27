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

package com.antgroup.openspg.server.infra.dao.repository.spgschema;

import com.antgroup.openspg.server.infra.dao.dataobject.ConstraintDO;
import com.antgroup.openspg.server.infra.dao.dataobject.ConstraintDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.ConstraintDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.convertor.ConstraintDOConvertor;
import com.antgroup.openspg.cloudext.interfaces.repository.sequence.SequenceRepository;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.server.schema.core.service.predicate.repository.ConstraintRepository;
import com.antgroup.openspg.server.core.schema.model.constraint.Constraint;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ConstraintRepositoryImpl implements ConstraintRepository {

  @Autowired private SequenceRepository sequenceRepository;
  @Autowired private ConstraintDOMapper constraintDOMapper;

  @Override
  public int upsert(Constraint constraint) {
    int cnt;
    ConstraintDO constraintDO = ConstraintDOConvertor.toConstraintDO(constraint);
    if (constraintDO.getId() == null) {
      constraintDO.setId(sequenceRepository.getSeqIdByTime());
      constraintDO.setGmtCreate(new Date());
      constraintDO.setGmtModified(new Date());
      cnt = constraintDOMapper.insert(constraintDO);
    } else {
      constraintDO.setGmtModified(new Date());
      cnt = constraintDOMapper.updateByPrimaryKeySelective(constraintDO);
    }

    constraint.setId(constraintDO.getId());
    return cnt;
  }

  @Override
  public int deleteById(Long id) {
    return constraintDOMapper.deleteByPrimaryKey(id);
  }

  @Override
  public List<Constraint> queryById(List<Long> constraintIds) {
    if (CollectionUtils.isEmpty(constraintIds)) {
      return Collections.emptyList();
    }

    ConstraintDOExample example = new ConstraintDOExample();
    example.createCriteria().andIdIn(constraintIds);
    List<ConstraintDO> constraintDOS = constraintDOMapper.selectByExampleWithBLOBs(example);
    return CollectionsUtils.listMap(constraintDOS, ConstraintDOConvertor::toConstraint);
  }

  @Override
  public int deleteById(List<Long> ids) {
    ConstraintDOExample example = new ConstraintDOExample();
    example.createCriteria().andIdIn(ids);
    return constraintDOMapper.deleteByExample(example);
  }
}
