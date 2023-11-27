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

package com.antgroup.openspg.server.schema.core.service.semantic.impl;

import com.antgroup.kg.reasoner.lube.parser.ParserInterface;
import com.antgroup.kg.reasoner.parser.KgDslParser;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.schema.core.service.semantic.LogicalRuleService;
import com.antgroup.openspg.server.schema.core.service.semantic.model.DslCheckResult;
import com.antgroup.openspg.server.schema.core.service.semantic.repository.LogicalRuleRepository;
import com.antgroup.openspg.server.core.schema.model.DslSyntaxError;
import com.antgroup.openspg.server.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.server.core.schema.model.semantic.RuleCode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogicalRuleServiceImpl implements LogicalRuleService {

  @Autowired private LogicalRuleRepository logicalRuleRepository;

  @Override
  public int create(LogicalRule rule) {
    DslCheckResult result = this.checkDslSyntax(rule.getContent());
    if (!result.isPass()) {
      throw DslSyntaxError.dslSyntaxError(result.getErrorPart());
    }

    if (rule.getCode() == null) {
      rule.setCode(new RuleCode(RuleCode.genRuleCode()));
    }
    int cnt = logicalRuleRepository.save(rule);
    log.info("add rule: {}", rule);
    return cnt;
  }

  @Override
  public int update(LogicalRule rule) {
    DslCheckResult result = this.checkDslSyntax(rule.getContent());
    if (!result.isPass()) {
      throw DslSyntaxError.dslSyntaxError(result.getErrorPart());
    }
    return logicalRuleRepository.update(rule);
  }

  @Override
  public int delete(LogicalRule logicalRule) {
    if (logicalRule.getCode() == null) {
      return 0;
    }

    return logicalRuleRepository.delete(logicalRule.getCode().getCode());
  }

  @Override
  public int deleteByRuleId(List<RuleCode> ruleCodes) {
    if (CollectionUtils.isEmpty(ruleCodes)) {
      return 0;
    }

    List<String> codes = ruleCodes.stream().map(RuleCode::getCode).collect(Collectors.toList());
    return logicalRuleRepository.delete(codes, null);
  }

  @Override
  public DslCheckResult checkDslSyntax(String dsl) {
    DslCheckResult result = new DslCheckResult();
    try {
      ParserInterface parser = new KgDslParser();
      if (StringUtils.isEmpty(dsl)) {
        result.setPass(false);
        result.setErrorPart("empty dsl");
        return result;
      }
      parser.parseMultipleStatement(dsl, null).iterator();
      return result;
    } catch (Exception ex) {
      result.setPass(false);
      result.setErrorPart(ex.getMessage());
      return result;
    }
  }

  @Override
  public List<LogicalRule> queryByRuleCode(List<RuleCode> ruleCodes) {
    if (CollectionUtils.isEmpty(ruleCodes)) {
      return Collections.emptyList();
    }

    List<String> codes = ruleCodes.stream().map(RuleCode::getCode).collect(Collectors.toList());
    return logicalRuleRepository.query(codes, true);
  }
}
