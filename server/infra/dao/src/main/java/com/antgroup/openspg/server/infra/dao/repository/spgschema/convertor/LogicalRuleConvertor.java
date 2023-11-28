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

package com.antgroup.openspg.server.infra.dao.repository.spgschema.convertor;

import com.antgroup.openspg.server.infra.dao.dataobject.LogicRuleDO;
import com.antgroup.openspg.common.model.UserInfo;
import com.antgroup.openspg.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.schema.model.semantic.RuleCode;
import com.antgroup.openspg.schema.model.semantic.RuleScopeEnum;
import com.antgroup.openspg.schema.model.semantic.RuleStatusEnum;

public class LogicalRuleConvertor {

  public static LogicRuleDO toDO(LogicalRule logicalRule) {
    if (null == logicalRule) {
      return null;
    }

    LogicRuleDO ruleDO = new LogicRuleDO();
    ruleDO.setRuleId(logicalRule.getCode() == null ? null : logicalRule.getCode().getCode());
    ruleDO.setName(logicalRule.getName());
    ruleDO.setIsMaster((byte) (Boolean.FALSE.equals(logicalRule.getMaster()) ? 0 : 1));
    ruleDO.setVersionId(logicalRule.getVersion() == null ? 1 : logicalRule.getVersion());
    ruleDO.setExpression(logicalRule.getContent());
    ruleDO.setStatus(
        logicalRule.getStatus() == null
            ? RuleStatusEnum.PROD.name()
            : logicalRule.getStatus().name());
    ruleDO.setUserNo(
        logicalRule.getCreator() == null ? "000000" : logicalRule.getCreator().getUserId());
    ruleDO.setEffectScope(RuleScopeEnum.QUERY.name());
    return ruleDO;
  }

  public static LogicalRule toModel(LogicRuleDO ruleDO) {
    if (null == ruleDO) {
      return null;
    }

    return new LogicalRule(
        new RuleCode(ruleDO.getRuleId()),
        ruleDO.getVersionId(),
        ruleDO.getName(),
        new Byte((byte) 1).equals(ruleDO.getIsMaster()),
        RuleStatusEnum.toEnum(ruleDO.getStatus()),
        ruleDO.getExpression(),
        new UserInfo(ruleDO.getUserNo(), null));
  }
}
