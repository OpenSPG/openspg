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

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ConstraintDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ConstraintDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ConstraintDOMapper {
  long countByExample(ConstraintDOExample example);

  int deleteByExample(ConstraintDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(ConstraintDO record);

  int insertSelective(ConstraintDO record);

  List<ConstraintDO> selectByExampleWithBLOBs(ConstraintDOExample example);

  List<ConstraintDO> selectByExample(ConstraintDOExample example);

  ConstraintDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") ConstraintDO record, @Param("example") ConstraintDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") ConstraintDO record, @Param("example") ConstraintDOExample example);

  int updateByExample(
      @Param("record") ConstraintDO record, @Param("example") ConstraintDOExample example);

  int updateByPrimaryKeySelective(ConstraintDO record);

  int updateByPrimaryKeyWithBLOBs(ConstraintDO record);

  int updateByPrimaryKey(ConstraintDO record);
}
