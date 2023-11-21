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

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.SysLockDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.SysLockDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysLockDOMapper {
  long countByExample(SysLockDOExample example);

  int deleteByExample(SysLockDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(SysLockDO record);

  int insertSelective(SysLockDO record);

  List<SysLockDO> selectByExample(SysLockDOExample example);

  SysLockDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") SysLockDO record, @Param("example") SysLockDOExample example);

  int updateByExample(
      @Param("record") SysLockDO record, @Param("example") SysLockDOExample example);

  int updateByPrimaryKeySelective(SysLockDO record);

  int updateByPrimaryKey(SysLockDO record);
}
