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

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OperatorVersionDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OperatorVersionDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OperatorVersionDOMapper {
  long countByExample(OperatorVersionDOExample example);

  int deleteByExample(OperatorVersionDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(OperatorVersionDO record);

  int insertSelective(OperatorVersionDO record);

  List<OperatorVersionDO> selectByExample(OperatorVersionDOExample example);

  OperatorVersionDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") OperatorVersionDO record,
      @Param("example") OperatorVersionDOExample example);

  int updateByExample(
      @Param("record") OperatorVersionDO record,
      @Param("example") OperatorVersionDOExample example);

  int updateByPrimaryKeySelective(OperatorVersionDO record);

  int updateByPrimaryKey(OperatorVersionDO record);
}
