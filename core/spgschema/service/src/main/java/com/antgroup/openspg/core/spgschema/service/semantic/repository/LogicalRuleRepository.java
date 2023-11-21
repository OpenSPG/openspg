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

package com.antgroup.openspg.core.spgschema.service.semantic.repository;

import com.antgroup.openspg.core.spgschema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.spgschema.model.semantic.RuleStatusEnum;
import java.util.List;

/** The read-write interface for logic rule, provides save、update、delete and query method. */
public interface LogicalRuleRepository {

  /**
   * Save a logic rule to db.
   *
   * @param logicalRule logic rule object
   * @return record count
   */
  int save(LogicalRule logicalRule);

  /**
   * Update logic rule content.
   *
   * @param logicalRule logic rule object
   * @return record count
   */
  int update(LogicalRule logicalRule);

  /**
   * Delete logic rule by rule code.
   *
   * @param ruleCode rule code
   * @return record count
   */
  int delete(String ruleCode);

  /**
   * Batch delete rules by code and status.
   *
   * @param ruleCodes list of rule code
   * @param status rule status
   * @return record count
   */
  int delete(List<String> ruleCodes, RuleStatusEnum status);

  /**
   * Query logic rule by rule code.
   *
   * @param ruleCodes list of rule code
   * @param isMaster if only query master rule
   * @return list of logic rule
   */
  List<LogicalRule> query(List<String> ruleCodes, Boolean isMaster);
}
