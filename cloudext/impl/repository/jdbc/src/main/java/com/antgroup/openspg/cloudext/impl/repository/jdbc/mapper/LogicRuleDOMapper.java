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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.mapper;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.LogicRuleDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.LogicRuleDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LogicRuleDOMapper {
  long countByExample(LogicRuleDOExample example);

  int deleteByExample(LogicRuleDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(LogicRuleDO record);

  int insertSelective(LogicRuleDO record);

  List<LogicRuleDO> selectByExampleWithBLOBs(LogicRuleDOExample example);

  List<LogicRuleDO> selectByExample(LogicRuleDOExample example);

  LogicRuleDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") LogicRuleDO record, @Param("example") LogicRuleDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") LogicRuleDO record, @Param("example") LogicRuleDOExample example);

  int updateByExample(
      @Param("record") LogicRuleDO record, @Param("example") LogicRuleDOExample example);

  int updateByPrimaryKeySelective(LogicRuleDO record);

  int updateByPrimaryKeyWithBLOBs(LogicRuleDO record);

  int updateByPrimaryKey(LogicRuleDO record);
}
