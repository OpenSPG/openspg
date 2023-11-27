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

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.LogicRuleDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.LogicRuleDOExample;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.mapper.LogicRuleDOMapper;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.convertor.LogicalRuleConvertor;
import com.antgroup.openspg.cloudext.interfaces.repository.sequence.SequenceRepository;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.core.spgschema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.spgschema.model.semantic.RuleStatusEnum;
import com.antgroup.openspg.core.spgschema.service.semantic.repository.LogicalRuleRepository;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LogicalRuleRepositoryImpl implements LogicalRuleRepository {

  @Autowired private SequenceRepository sequenceRepository;
  @Autowired private LogicRuleDOMapper logicalRuleDOMapper;

  @Override
  public int save(LogicalRule logicalRule) {
    LogicRuleDO logicalRuleDO = LogicalRuleConvertor.toDO(logicalRule);
    logicalRuleDO.setId(sequenceRepository.getSeqIdByTime());
    logicalRuleDO.setGmtCreate(new Date());
    logicalRuleDO.setGmtModified(new Date());
    return logicalRuleDOMapper.insert(logicalRuleDO);
  }

  @Override
  public int update(LogicalRule logicalRule) {
    LogicRuleDO logicalRuleDO = LogicalRuleConvertor.toDO(logicalRule);
    logicalRuleDO.setGmtModified(new Date());

    LogicRuleDOExample example = new LogicRuleDOExample();
    LogicRuleDOExample.Criteria criteria =
        example.createCriteria().andRuleIdEqualTo(logicalRule.getCode().getCode());

    if (logicalRule.getVersion() == null) {
      criteria.andIsMasterEqualTo((byte) 1);
    } else {
      criteria.andVersionIdEqualTo(logicalRule.getVersion());
    }
    return logicalRuleDOMapper.updateByExampleSelective(logicalRuleDO, example);
  }

  @Override
  public int delete(String ruleId) {
    LogicRuleDOExample example = new LogicRuleDOExample();
    example.createCriteria().andRuleIdEqualTo(ruleId);
    return logicalRuleDOMapper.deleteByExample(example);
  }

  @Override
  public int delete(List<String> ruleIds, RuleStatusEnum status) {
    LogicRuleDOExample example = new LogicRuleDOExample();
    LogicRuleDOExample.Criteria criteria = example.createCriteria().andRuleIdIn(ruleIds);

    if (status != null) {
      criteria.andStatusEqualTo(status.name());
    }
    return logicalRuleDOMapper.deleteByExample(example);
  }

  @Override
  public List<LogicalRule> query(List<String> ruleIds, Boolean isMaster) {
    LogicRuleDOExample example = new LogicRuleDOExample();
    example
        .createCriteria()
        .andRuleIdIn(ruleIds)
        .andIsMasterEqualTo((byte) (Boolean.FALSE.equals(isMaster) ? 0 : 1));
    List<LogicRuleDO> logicalRuleDOS = logicalRuleDOMapper.selectByExampleWithBLOBs(example);
    return CollectionsUtils.listMap(logicalRuleDOS, LogicalRuleConvertor::toModel);
  }
}
