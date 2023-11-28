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

package com.antgroup.openspg.server.core.schema.service.semantic;

import com.antgroup.openspg.server.core.schema.service.semantic.model.DslCheckResult;
import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.schema.model.semantic.RuleCode;
import java.util.List;

/** Semantic rule domain service, provide save、update、query、delete method of rule. */
public interface LogicalRuleService {

  /**
   * add a new rule.
   *
   * @param rule rule info
   * @return rule info
   */
  int create(LogicalRule rule);

  /**
   * Update rule detail.
   *
   * @param logicalRule rule info
   * @return record count
   */
  int update(LogicalRule logicalRule);

  /**
   * Delete the logic rule by rule code.
   *
   * @param logicalRule rule info
   * @return record count
   */
  int delete(LogicalRule logicalRule);

  /**
   * Batch delete logic rule by rule code.
   *
   * @param ruleCodes list of rule code
   * @return record count
   */
  int deleteByRuleId(List<RuleCode> ruleCodes);

  /**
   * Batch query logic rule by rule code.
   *
   * @param ruleCodes list of rule code
   * @return list of logic rule
   */
  List<LogicalRule> queryByRuleCode(List<RuleCode> ruleCodes);

  /**
   * Check if the dsl has syntax error.
   *
   * @param dsl the dsl content
   * @return check result
   */
  DslCheckResult checkDslSyntax(String dsl);
}
